package net.zylk.sign.online.webscripts;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import net.zylk.sign.online.webscripts.utils.HttpUtils;
import net.zylk.sign.online.webscripts.utils.PropertiesManager;

public class UploadSignedContentWebscript extends AbstractWebScript {

	private static Log logger = LogFactory.getLog(UploadSignedContentWebscript.class);

	private ServiceRegistry registry;
	private PropertiesManager propertiesManager;

	@Override	
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		String nodeRefparam = req.getParameter("nodeRef");		
		String token = req.getParameter("token");
		NodeRef nodeRef = new NodeRef(nodeRefparam);
		String documentName = (String)registry.getNodeService().getProperties(nodeRef).get(ContentModel.PROP_NAME);		
		ChildAssociationRef childAssociationRef = registry.getNodeService().getPrimaryParent(nodeRef);		
		NodeRef folderNodeRef = childAssociationRef.getParentRef();
		logger.debug("[folderNodeRef=" + folderNodeRef +",token="+token+",nodeRefparam="+nodeRefparam+"]");	
		ContentReader nodeContent = getReader(registry, nodeRef);
		String mimetype = nodeContent.getMimetype();		
		if (mimetype.equals(MimetypeMap.MIMETYPE_PDF)) {
				
		} else {
			
		}

	}
	
	private static ContentReader getReader(ServiceRegistry registry, NodeRef nodeRef) {	
		// First check that the node is a sub-type of content
		QName typeQName = registry.getNodeService().getType(nodeRef);
		if (registry.getDictionaryService().isSubClass(typeQName, ContentModel.TYPE_CONTENT) == false) {
			// it is not content, so can't transform
			return null;
		}
		// Get the content reader
		ContentReader contentReader = registry.getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);
		return contentReader;
	}


	

	public ServiceRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(ServiceRegistry registry) {
		this.registry = registry;
	}

	public void setPropertiesManager(PropertiesManager propertiesManager) {
		this.propertiesManager = propertiesManager;
	}

}