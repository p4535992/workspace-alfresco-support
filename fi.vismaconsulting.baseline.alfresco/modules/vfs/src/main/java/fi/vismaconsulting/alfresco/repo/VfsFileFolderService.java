package fi.vismaconsulting.alfresco.repo;

import org.alfresco.query.EmptyPagingResults;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.node.getchildren.FilterProp;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.site.SiteServiceException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.*;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: Peter Mikula <peter.mikula@proactum.fi>
 * Created: 3/30/15 1:32 PM
 */
public class VfsFileFolderService implements FileFolderService {

    private NodeService nodeService;
    private SiteService siteService;
    private DictionaryService dictionaryService;
    private FileFolderService fileFolderService;

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    // ---

    @Override
    public List<FileInfo> list(NodeRef contextNodeRef) {

        if (isSite(contextNodeRef)) {
            contextNodeRef = getDocumentLibrary(contextNodeRef);
            if (contextNodeRef == null) {
                return Collections.emptyList();
            }
        }

        return fileFolderService.list(contextNodeRef);
    }

    @Override
    public PagingResults<FileInfo> list(NodeRef contextNodeRef, boolean files, boolean folders, Set<QName> ignoreTypeQNames, List<Pair<QName, Boolean>> sortProps, PagingRequest pagingRequest) {

        if (isSite(contextNodeRef)) {
            contextNodeRef = getDocumentLibrary(contextNodeRef);
            if (contextNodeRef == null) {
                return new EmptyPagingResults<FileInfo>();
            }
        }

        return fileFolderService.list(contextNodeRef, files, folders, ignoreTypeQNames, sortProps, pagingRequest);
    }

    @Override
    public PagingResults<FileInfo> list(NodeRef contextNodeRef, boolean files, boolean folders, String pattern, Set<QName> ignoreTypeQNames, List<Pair<QName, Boolean>> sortProps, PagingRequest pagingRequest) {
        if (isSite(contextNodeRef)) {
            contextNodeRef = getDocumentLibrary(contextNodeRef);
            if (contextNodeRef == null) {
                return new EmptyPagingResults<FileInfo>();
            }
        }

        return fileFolderService.list(contextNodeRef, files, folders, pattern, ignoreTypeQNames, sortProps, pagingRequest);
    }

    @Override
    public PagingResults<FileInfo> list(NodeRef rootNodeRef, Set<QName> searchTypeQNames, Set<QName> ignoreAspectQNames, List<Pair<QName, Boolean>> sortProps, PagingRequest pagingRequest) {
        if (isSite(rootNodeRef)) {
            rootNodeRef = getDocumentLibrary(rootNodeRef);
            if (rootNodeRef == null) {
                return new EmptyPagingResults<FileInfo>();
            }
        }
        return fileFolderService.list(rootNodeRef, searchTypeQNames, ignoreAspectQNames, sortProps, pagingRequest);
    }

    @Override
    public PagingResults<FileInfo> list(NodeRef rootNodeRef, Set<QName> assocTypeQNames, Set<QName> searchTypeQNames, Set<QName> ignoreAspectQNames, List<Pair<QName, Boolean>> sortProps, List<FilterProp> filterProps, PagingRequest pagingRequest) {
        if (isSite(rootNodeRef)) {
            rootNodeRef = getDocumentLibrary(rootNodeRef);
            if (rootNodeRef == null) {
                return new EmptyPagingResults<FileInfo>();
            }
        }
        return fileFolderService.list(rootNodeRef, assocTypeQNames, searchTypeQNames, ignoreAspectQNames, sortProps, filterProps, pagingRequest);
    }

    @Override
    public List<FileInfo> toFileInfoList(List<NodeRef> nodeRefs) {
        return nodeRefs.stream().map(this::getFileInfo).collect(Collectors.toList());
    }

    @Override
    public List<FileInfo> listFiles(NodeRef contextNodeRef) {
        if (isSite(contextNodeRef)) {
            contextNodeRef = getDocumentLibrary(contextNodeRef);
            if (contextNodeRef == null) {
                return Collections.emptyList();
            }
        }

        return fileFolderService.listFiles(contextNodeRef);
    }

    @Override
    public List<FileInfo> listFolders(NodeRef contextNodeRef) {
        if (isSite(contextNodeRef)) {
            contextNodeRef = getDocumentLibrary(contextNodeRef);
            if (contextNodeRef == null) {
                return Collections.emptyList();
            }
        }

        return fileFolderService.listFolders(contextNodeRef);
    }

    @Override
    public List<FileInfo> listDeepFolders(NodeRef contextNodeRef, SubFolderFilter filter) {
        throw new UnsupportedOperationException();
        //return null;
    }

    @Override
    public NodeRef getLocalizedSibling(NodeRef nodeRef) {
        // site and documentLibrary is not localized
        return fileFolderService.getLocalizedSibling(nodeRef);
    }

    @Override
    public NodeRef searchSimple(NodeRef contextNodeRef, String name) {

        if (isSite(contextNodeRef)) {
            contextNodeRef = getDocumentLibrary(contextNodeRef);
            if (contextNodeRef == null) {
                return null;
            }
        }

        return fileFolderService.searchSimple(contextNodeRef, name);
    }

    @Override
    public List<FileInfo> search(NodeRef contextNodeRef, String namePattern, boolean includeSubFolders) {
        if (includeSubFolders) {
            // FIXME:
            throw new UnsupportedOperationException();
        }

        if (isSite(contextNodeRef)) {
            contextNodeRef = getDocumentLibrary(contextNodeRef);
            if (contextNodeRef == null) {
                return Collections.emptyList();
            }
        }

        return fileFolderService.search(contextNodeRef, namePattern, false);
    }

    @Override
    public List<FileInfo> search(NodeRef contextNodeRef, String namePattern, boolean fileSearch, boolean folderSearch, boolean includeSubFolders) {
        if (includeSubFolders) {
            // FIXME:
            throw new UnsupportedOperationException();
        }

        if (isSite(contextNodeRef)) {
            contextNodeRef = getDocumentLibrary(contextNodeRef);
            if (contextNodeRef == null) {
                return Collections.emptyList();
            }
        }

        return fileFolderService.search(contextNodeRef, namePattern, fileSearch, folderSearch, false);
    }

    @Override
    public FileInfo copy(NodeRef sourceNodeRef, NodeRef targetParentRef, String newName) throws FileExistsException, FileNotFoundException {

        if (targetParentRef != null && isSite(targetParentRef)) {
            NodeRef nodeRef = getDocumentLibrary(targetParentRef);
            if (nodeRef == null) {
                throw new FileNotFoundException(targetParentRef);
            }
            targetParentRef = nodeRef;
        }

        return fileFolderService.copy(sourceNodeRef, targetParentRef, newName);
    }

    @Override
    public FileInfo moveFrom(NodeRef sourceNodeRef, NodeRef sourceParentRef, NodeRef targetParentRef, String newName) throws FileExistsException, FileNotFoundException {

        if (sourceParentRef != null && isSite(sourceParentRef)) {
            NodeRef nodeRef = getDocumentLibrary(sourceParentRef);
            if (nodeRef == null) {
                throw new FileNotFoundException(sourceParentRef);
            }
            sourceParentRef = nodeRef;
        }

        if (targetParentRef != null && isSite(targetParentRef)) {
            NodeRef nodeRef = getDocumentLibrary(targetParentRef);
            if (nodeRef == null) {
                throw new FileNotFoundException(targetParentRef);
            }
            targetParentRef = nodeRef;
        }

        return fileFolderService.moveFrom(sourceNodeRef, sourceParentRef, targetParentRef, newName);
    }

    @Override
    public FileInfo move(NodeRef sourceNodeRef, NodeRef targetParentRef, String newName) throws FileExistsException, FileNotFoundException {
        return moveFrom(sourceNodeRef, null, targetParentRef, newName);
    }

    @Override
    public FileInfo move(NodeRef sourceNodeRef, NodeRef sourceParentRef, NodeRef targetParentRef, String newName) throws FileExistsException, FileNotFoundException {
        return moveFrom(sourceNodeRef, sourceParentRef, targetParentRef, newName);
    }

    @Override
    public FileInfo rename(NodeRef fileFolderRef, String newName) throws FileExistsException, FileNotFoundException {
        return moveFrom(fileFolderRef, null, null, newName);
    }

    @Override
    public FileInfo create(NodeRef parentNodeRef, String name, QName typeQName) throws FileExistsException {
        return create(parentNodeRef, name, typeQName, null);
    }

    @Override
    public FileInfo create(NodeRef parentNodeRef, String name, QName typeQName, QName assocQName) throws FileExistsException {
        if (parentNodeRef != null && isSite(parentNodeRef)) {
            NodeRef nodeRef = getDocumentLibrary(parentNodeRef);
            if (nodeRef == null) {
                throw new InvalidNodeRefException(parentNodeRef);
            }
            parentNodeRef = nodeRef;
        }
        return fileFolderService.create(parentNodeRef, name, typeQName, assocQName);
    }

    @Override
    public void delete(NodeRef nodeRef) {
        if (isSite(nodeRef) || isSiteRoot(nodeRef)) {
            throw new AccessDeniedException("Denied by VFS policy.");
        }
        fileFolderService.delete(nodeRef);
    }

    @Override
    public List<FileInfo> getNamePath(NodeRef rootNodeRef, NodeRef nodeRef) throws FileNotFoundException {
        List<FileInfo> results = new ArrayList<FileInfo>(24);
        for (FileInfo info : fileFolderService.getNamePath(rootNodeRef, nodeRef)) {
            if (!nodeService.hasAspect(info.getNodeRef(), SiteModel.ASPECT_SITE_CONTAINER)) {
                results.add(info);
            }
        }
        return results;
    }

    @Override
    public List<String> getNameOnlyPath(NodeRef rootNodeRef, NodeRef nodeRef) throws FileNotFoundException {
        List<FileInfo> path = getNamePath(rootNodeRef, nodeRef);
        Iterator<FileInfo> it = path.iterator();
        if (!it.hasNext()) {
            throw new FileNotFoundException(nodeRef);
        }

        //skip root node
        //it.next();

        List<String> results = new ArrayList<String>(path.size());
        while (it.hasNext()) {
            results.add(it.next().getName());
        }
        return results;
    }

    @Override
    public FileInfo resolveNamePath(NodeRef rootNodeRef, List<String> pathElements) throws FileNotFoundException {
        return resolveNamePath(rootNodeRef, pathElements, true);
    }

    @Override
    public FileInfo resolveNamePath(NodeRef rootNodeRef, List<String> pathElements, boolean mustExist) throws FileNotFoundException {
        if (pathElements.isEmpty()) {
            throw new IllegalArgumentException("Path elements list is empty");
        }

        NodeRef nodeRef = rootNodeRef;
        for (String nodeName : pathElements) {
            nodeRef = searchSimple(nodeRef, nodeName);
            if (nodeRef == null) {
                if (mustExist) {
                    throw new FileNotFoundException("Folder not found.");
                }
                return null;
            }
        }

        return getFileInfo(nodeRef);
    }

    @Override
    public FileInfo getFileInfo(NodeRef nodeRef) {
        if (nodeService.hasAspect(nodeRef, SiteModel.ASPECT_SITE_CONTAINER)) {
            nodeRef = nodeService.getPrimaryParent(nodeRef).getParentRef();
        }
        return fileFolderService.getFileInfo(nodeRef);
    }

    @Override
    public ContentReader getReader(NodeRef nodeRef) {
        return fileFolderService.getReader(nodeRef);
    }

    @Override
    public ContentWriter getWriter(NodeRef nodeRef) {
        return fileFolderService.getWriter(nodeRef);
    }

    @Override
    public boolean exists(NodeRef nodeRef) {
        boolean exists = fileFolderService.exists(nodeRef);
        if (exists && isSite(nodeRef)) {
            exists = getDocumentLibrary(nodeRef) != null;
        }
        return exists;
    }

    @Override
    public FileFolderServiceType getType(QName typeQName) {
        return fileFolderService.getType(typeQName);
    }

    @Override
    public boolean isHidden(NodeRef nodeRef) {
        return fileFolderService.isHidden(nodeRef);
    }

    @Override
    public void setHidden(NodeRef nodeRef, boolean isHidden) {
        fileFolderService.setHidden(nodeRef, isHidden);
    }

    // ---

    protected boolean isSiteRoot(NodeRef nodeRef) {
        QName type = nodeService.getType(nodeRef);
        return dictionaryService.isSubClass(type, SiteModel.TYPE_SITES);
    }

    protected boolean isSite(NodeRef nodeRef) {
        QName type = nodeService.getType(nodeRef);
        return dictionaryService.isSubClass(type, SiteModel.TYPE_SITE);
    }

    protected NodeRef getDocumentLibrary(NodeRef nodeRef) {
        try {
            FileInfo fileInfo = fileFolderService.resolveNamePath(nodeRef,
                    Collections.singletonList("documentLibrary"));
            if (!fileInfo.isFolder()) {
                throw new SiteServiceException("site_service.site_container_not_folder",
                        new Object[]{fileInfo.getName()});
            }
            return fileInfo.getNodeRef();
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
