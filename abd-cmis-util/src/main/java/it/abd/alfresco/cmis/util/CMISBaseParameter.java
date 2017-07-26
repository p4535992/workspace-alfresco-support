package it.abd.alfresco.cmis.util;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

import it.abd.alfresco.cmis.base.BaseParameter;


/**
 * This class holds common properties and methods for the example classes.
 * 
 * @author jpotts
 */
public class CMISBaseParameter extends BaseParameter {
	
	//private String CMIS_URL = "https://cmis.alfresco.com/cmisws/cmis?wsdl"; // Uncomment for Web Services binding
    //private String CMIS_URL = "http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.0/atom"; // Uncomment for Atom Pub binding
	//private String CMIS_URL = "https://209.132.183.150/alfresco/api/-default-/public/cmis/versions/1.1/browser"; // Uncomment for Browser binding
    
	// private final String CMIS_URL = "http://server2137:8180/alfresco/api/-default-/public/cmis/versions/1.1/atom";
	private String CMIS_URL = "http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/atom"; // Uncomment for Atom Pub binding

    private static final String CM_TITLE = "cm:title";
    private static final String CM_DESCRIPTION = "cm:description";
    private static final String TEST_TITLE = "this is my title";
    private static final String TEST_DESCRIPTION = "this is my description";
    
    private Session session = null;

    private String contentType;
    private String contentName;

	public Session getSession() {

		if (session == null) {
			// default factory implementation
			SessionFactory factory = SessionFactoryImpl.newInstance();
			Map<String, String> parameter = new HashMap<String, String>();
	
			// user credentials
			parameter.put(SessionParameter.USER, getUser());
			parameter.put(SessionParameter.PASSWORD, getPassword());
	
			// connection settings
			parameter.put(SessionParameter.ATOMPUB_URL, getServiceUrl()); // Uncomment for Atom Pub binding
			parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value()); // Uncomment for Atom Pub binding

			//parameter.put(SessionParameter.BROWSER_URL, getServiceUrl()); // Uncomment for Browser binding
			//parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value()); // Uncomment for Browser binding
			
			// Set the alfresco object factory
			// Used when using the CMIS extension for Alfresco for working with aspects and CMIS 1.0
			// This is not needed when using CMIS 1.1
			//parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
			
			List<Repository> repositories = factory.getRepositories(parameter);
			Repository repository = repositories.get(0);
			this.session = repository.createSession();
		}
		return this.session;
	}

	/**
	 * Gets the object ID for a folder of a specified name which is assumed to be unique across the
	 * entire repository.
	 * 
	 * @return String
	 */
	public String getFolderId(String folderName) {
		String objectId = null;
		String queryString = "select cmis:objectId from cmis:folder where cmis:name = '" + folderName + "'";
    	ItemIterable<QueryResult> results = getSession().query(queryString, false);
    	for (QueryResult qResult : results) {
    		objectId = qResult.getPropertyValueByQueryName("cmis:objectId");
    	}
    	return objectId;
	}
	
	public Folder getTestFolder(String folderPath) throws CmisObjectNotFoundException {
		Session session = getSession();

	    	// Grab a reference to the folder where we want to create content
		Folder folder = null;
		try {
			folder = (Folder) session.getObjectByPath(folderPath);
			System.out.println("Found folder: " + folder.getName() + "(" + folder.getId() + ")");
		} catch (CmisObjectNotFoundException confe) {
			Folder targetBaseFolder = null;
			String baseFolderPath = folderPath.substring(0, folderPath.lastIndexOf('/') + 1);
			String folderName = folderPath.substring(folderPath.lastIndexOf('/') + 1);
				
			//if this one is not found, we'll let the exception bubble up
			targetBaseFolder = (Folder) session.getObjectByPath(baseFolderPath);

			// Create a Map of objects with the props we want to set
			Map <String, Object> properties = new HashMap<String, Object>();
				
			// Following sets the content type and adds the webable and productRelated aspects
			// This works because we are using the OpenCMIS extension for Alfresco
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder, P:cm:titled");
			properties.put(PropertyIds.NAME, folderName);

			properties.put(CM_DESCRIPTION, TEST_DESCRIPTION);
			properties.put(CM_TITLE, TEST_TITLE);		
				
			folder = targetBaseFolder.createFolder(properties);
			System.out.println("Created folder: " + folder.getName() + " (" + folder.getId() + ")");
		}		
				
		return folder;
	}
	
	public Document createTestDoc(String docName, String contentType) {
		//e.g. test, whitepaper
		Session session = getSession();
		
    	// Grab a reference to the folder where we want to create content
		Folder folder = (Folder) session.getObjectByPath("/" + getFolderName());
		
		String timeStamp = new Long(System.currentTimeMillis()).toString();
		String filename = docName + " (" + timeStamp + ")";
		
		// Create a Map of objects with the props we want to set
		Map <String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.NAME, filename);

		// To set the content type and add the webable and productRelated aspects
		// using CMIS 1.0 and the OpenCMIS extension for Alfresco, do this:
		//properties.put(PropertyIds.OBJECT_TYPE_ID, "D:sc:whitepaper,P:sc:webable,P:sc:productRelated");		
		//properties.put(PropertyIds.NAME, filename);
		
		// To set the content type and add the webable and productRelated aspects
		// using CMIS 1.1 which has aspect support natively, do this:
		properties.put(PropertyIds.OBJECT_TYPE_ID, "D:sc:whitepaper");
		
		properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, Arrays.asList("P:sc:webable", "P:sc:productRelated", "P:cm:generalclassifiable"));
		properties.put("sc:isActive", true);
		GregorianCalendar publishDate = new GregorianCalendar(2007,4,1,5,0);
		properties.put("sc:published", publishDate);

		String docText = "This is a sample " + contentType + " document called " + docName;
		byte[] content = docText.getBytes();
		InputStream stream = new ByteArrayInputStream(content);
		ContentStream contentStream = session.getObjectFactory().createContentStream(filename, Long.valueOf(content.length), "text/plain", stream);

		Document doc = folder.createDocument(
				   properties,
				   contentStream,
				   VersioningState.MAJOR);
		System.out.println("Created: " + doc.getId());
		System.out.println("Content Length: " + doc.getContentStreamLength());
		
		return doc;
	}

	public String getServiceUrl() {
		return CMIS_URL;
	}

	public void setServiceUrl(String serviceUrl) {
		this.CMIS_URL = serviceUrl;
	}

    public String getContentName() {
		return this.contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}		
}
