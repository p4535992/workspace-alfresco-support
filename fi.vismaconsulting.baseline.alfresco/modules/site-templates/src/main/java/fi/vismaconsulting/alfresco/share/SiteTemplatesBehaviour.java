package fi.vismaconsulting.alfresco.share;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.support.DataAccessUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * Author: Peter Mikula <peter.mikula@proactum.fi>
 * Created: 3/6/15 1:07 PM
 */
public class SiteTemplatesBehaviour implements NodeServicePolicies.OnCreateNodePolicy, InitializingBean {

    private NodeService nodeService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }


    private ContentService contentService;

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    private CopyService copyService;

    public void setCopyService(CopyService copyService) {
        this.copyService = copyService;
    }

    private SiteService siteService;

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    private PolicyComponent policyComponent;

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

    private PermissionService permissionService;

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public void afterPropertiesSet() {
        policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnCreateNodePolicy.QNAME,
                SiteModel.TYPE_SITE,
                new JavaBehaviour(this, "onCreateNode", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
    }

    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        NodeRef nodeRef = childAssocRef.getChildRef();

        // tx commit, node might have been already removed...
        if (!nodeService.exists(nodeRef)) {
            return;
        }

        SiteInfo targetSite = siteService.getSite(nodeRef);

        SiteInfo sourceSite = getSourceSite(targetSite);

        if (sourceSite != null) {
            copyContainers(targetSite, sourceSite);
            copyPermissions(targetSite, sourceSite);
            copyConfiguration(targetSite, sourceSite);
        }
    }

    protected SiteInfo getSourceSite(SiteInfo targetSite) {
        SiteInfo sourceSite = null;
        String sitePreset = targetSite.getSitePreset();
        if (sitePreset != null && sitePreset.startsWith("template-")) {
            sourceSite = siteService.getSite(sitePreset);
        }
        return sourceSite;
    }

    protected void copyContainers(SiteInfo targetSite, SiteInfo sourceSite) {
        for (ChildAssociationRef assoc : nodeService.getChildAssocs(sourceSite.getNodeRef(),
                ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL, true)) {
            NodeRef nodeRef = assoc.getChildRef();
            if (nodeService.hasAspect(nodeRef, SiteModel.ASPECT_SITE_CONTAINER)) {

                QName typeQName = nodeService.getType(nodeRef);
                Map<QName, Serializable> props = nodeService.getProperties(nodeRef);
                String componentId = (String) props.get(SiteModel.PROP_COMPONENT_ID);

                // we cannot copy container directly as they cannot be renamed
                NodeRef copy = siteService.createContainer(targetSite.getShortName(), componentId, typeQName, props);
                copyChildren(copy, nodeRef);
            }
        }
    }

    protected void copyPermissions(SiteInfo targetSite, SiteInfo sourceSite) {
        for (AccessPermission perm : permissionService.getAllSetPermissions(sourceSite.getNodeRef())) {

            if (!perm.isSetDirectly()) {
                continue;
            }

            AuthorityType type = perm.getAuthorityType();
            if (type != AuthorityType.GROUP && type != AuthorityType.USER) {
                continue;
            }

            // traditional site group
            if (perm.getAuthority().startsWith("GROUP_site")) {
                continue;
            }

            siteService.setMembership(targetSite.getShortName(), perm.getAuthority(), perm.getPermission());
        }
    }

    protected void copyChildren(NodeRef target, NodeRef source) {
        for (ChildAssociationRef assoc : nodeService.getChildAssocs(source,
                ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL, true)) {
            NodeRef copy = copyService.copy(assoc.getChildRef(), target, assoc.getTypeQName(), assoc.getQName(), true);
            String name = (String) nodeService.getProperty(assoc.getChildRef(), ContentModel.PROP_NAME);
            nodeService.setProperty(copy, ContentModel.PROP_NAME, name);
        }
    }

    private static final QName QNAME_SURF_CONFIG = QName.createQName(
            NamespaceService.CONTENT_MODEL_1_0_URI, "surf-config");

    protected void copyConfiguration(SiteInfo targetSite, SiteInfo sourceSite) {
        ChildAssociationRef surfConfigAssoc = DataAccessUtils.singleResult(nodeService
                .getChildAssocs(sourceSite.getNodeRef(), ContentModel.ASSOC_CONTAINS, QNAME_SURF_CONFIG));
        recurCopyConfig(targetSite.getNodeRef(), surfConfigAssoc, targetSite);
    }

    protected void recurCopyConfig(NodeRef target, ChildAssociationRef assocRef, SiteInfo targetSite) {

        QName typeQName = assocRef.getTypeQName();
        QName assocQName = migrateName(assocRef.getQName(), targetSite);

        NodeRef source = assocRef.getChildRef();
        NodeRef copy = copyService.copy(source, target, typeQName, assocQName, false);

        // this is not copied
        String name = (String) nodeService.getProperty(source, ContentModel.PROP_NAME);
        nodeService.setProperty(copy, ContentModel.PROP_NAME, migrateName(name, targetSite));

        migrateProps(copy, targetSite);
        migrateContent(copy, source, targetSite);

        // we do not want this...
        nodeService.removeAspect(copy, ContentModel.ASPECT_COPIEDFROM);

        for (ChildAssociationRef assoc : nodeService.getChildAssocs(source,
                ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL, true)) {
            recurCopyConfig(copy, assoc, targetSite);
        }
    }

    protected String migrateName(String name, SiteInfo siteInfo) {
        String newSiteName = siteInfo.getShortName();
        String oldSiteName = siteInfo.getSitePreset();

        if (name.equals(oldSiteName)) {
            name = newSiteName;
        } else if (name.contains(".site~")) {
            name = name.replace(
                    ".site~" + oldSiteName + "~",
                    ".site~" + newSiteName + "~");
        }
        return name;
    }

    protected QName migrateName(QName qname, SiteInfo siteInfo) {
        String localName = qname.getLocalName();
        String newLocalName = migrateName(localName, siteInfo);
        return localName.equals(newLocalName) ? qname
                : QName.createQName(qname.getNamespaceURI(), newLocalName);
    }

    protected void migrateProps(NodeRef nodeRef, SiteInfo siteInfo) {
        Map<QName, Serializable> props = nodeService.getProperties(nodeRef);
        for (Map.Entry<QName, Serializable> entry : props.entrySet()) {
            if (entry.getValue() instanceof String) {
                String value = (String) entry.getValue();
                entry.setValue(migrateName(value, siteInfo));
            }
        }
    }

    private void migrateContent(NodeRef target, NodeRef source, SiteInfo siteInfo) {
        // migrate content
        ContentReader reader = contentService.getReader(source, ContentModel.PROP_CONTENT);
        if (reader == null) {//folder?
            return;
        }

        ContentWriter writer = contentService.getWriter(target, ContentModel.PROP_CONTENT, true);

        String content = reader.getContentString();
        String newContent = migrateName(content, siteInfo);
        if (newContent.contains("site/")) {
            newContent = newContent.replace(
                    "site/" + siteInfo.getSitePreset() + "/",
                    "site/" + siteInfo.getShortName() + "/");
        }

        newContent = migrateSiteLogo(newContent, siteInfo);

        if (!newContent.equals(content)) {
            writer.putContent(newContent);
        }
    }

    private String migrateSiteLogo(String content, SiteInfo siteInfo) {
        if (!content.contains("<siteLogo>")) {
            return content;
        }

        try {
            Document document = DocumentHelper.parseText(content);
            XPath xpath = document.createXPath("/page/properties/siteLogo");
            Element siteLogo = (Element) xpath.selectSingleNode(document);
            if (siteLogo == null) {
                return content;
            }

            NodeRef sourceNode = new NodeRef(siteLogo.getTextTrim());
            if (!nodeService.exists(sourceNode)) {
                return content;
            }

            ChildAssociationRef assoc = nodeService.getPrimaryParent(sourceNode);
            NodeRef copy = copyService.copy(assoc.getChildRef(), siteInfo.getNodeRef(),
                    assoc.getTypeQName(), assoc.getQName(), true);
            String name = (String) nodeService.getProperty(assoc.getChildRef(), ContentModel.PROP_NAME);
            nodeService.setProperty(copy, ContentModel.PROP_NAME, name);

            siteLogo.setText(copy.toString());
            content = document.asXML();

        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Failed to migrate logo to %s", siteInfo.getShortName()), e);
            } else if (logger.isWarnEnabled()) {
                logger.warn(String.format("Failed to migrate logo to %s", siteInfo.getShortName()));
            }
        }
        return content;
    }

    private final Log logger = LogFactory.getLog(SiteTemplatesBehaviour.class);
}
