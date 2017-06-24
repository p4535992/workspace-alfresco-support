package it.abd.alfresco.api;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;

public interface ExampleBaseIfc {

    public Session getCmisSession() throws Exception;

    public Folder getParentFolder(Session cmisSession) throws Exception;

    public String getObjectTypeId();

}
