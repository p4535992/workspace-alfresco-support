package it.abd.alfresco.cmis.client;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;

public class CmisClient {

	/**
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		//CREATING CONTENT WITH CMIS
				
		//The first thing that we need to do is to create a session to Alfresco:
		SessionFactory factory = SessionFactoryImpl.newInstance(); 
		Map<String, String> parameter = new HashMap<String, String>(); 
		parameter.put(SessionParameter.USER, "admin"); 
		parameter.put(SessionParameter.PASSWORD, "admin"); 
		parameter.put(SessionParameter.ATOMPUB_URL, "http://127.0.0.1:8787/alfresco/api/-default-/public/cmis/versions/1.1/atom"); 
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value()); 
		parameter.put(SessionParameter.REPOSITORY_ID, "-default-"); 
		Session session = factory.createSession(parameter); 
		//Then, locate the document library folder of the Marketing Site that we created previously:
		String path = "/Sites/marketing/documentLibrary"; 
		Folder documentLibrary = (Folder) session.getObjectByPath(path); 
		
		//In the document library, we try to locate the Marketing folder:
		Folder marketingFolder = null; 
		for (CmisObject child :documentLibrary.getChildren()) { 
		   if ("Marketing".equals(child.getName())) { 
		         marketingFolder = (Folder) child; 
		   } 
		} 
		//We have to consider that this folder may not exist. So, we need to create it on the fly:
		// create the marketing folder if needed 
		if (marketingFolder == null) { 
		   Map<String, Object> properties = new HashMap<String, Object>(); 
		   properties.put(PropertyIds.NAME, "Marketing"); 
		   properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder"); 
		   marketingFolder = documentLibrary.createFolder(properties); 
		} 
		//Now, we are now ready to prepare the creation of our new document. 
		//The first step is to prepare the required properties:
		// prepare properties 
		String filename = "My new whitepaper.txt"; 
		Map<String, Object> properties = new HashMap<String, Object>(); 
		properties.put(PropertyIds.NAME, filename); 
		properties.put(PropertyIds.OBJECT_TYPE_ID, "D:sc:marketingDoc"); 
		//Then, we can prepare the content. In this sample, we'll create a text file:
		// prepare content 
		String content = "Hello World!"; 
		String mimetype = "text/plain; charset=UTF-8"; 
		byte[] contentBytes = content.getBytes("UTF-8"); 
		ByteArrayInputStream stream = new ByteArrayInputStream(contentBytes); 
		ContentStream contentStream = session.getObjectFactory().createContentStream(filename, contentBytes.length, mimetype, stream); 
		
		//Finally, we are ready to create the document:
		// create the document 
		Document marketingDocument = null;
		try{
		    marketingDocument = marketingFolder.createDocument(properties, contentStream, VersioningState.MAJOR); 
		}catch( org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException e){
			System.out.println("The file already exists!");
			for (CmisObject child :marketingFolder.getChildren()) { 
				   if (child.getType().getId().equals("D:sc:marketingDoc")) 
					   marketingDocument = (Document) child; 
			} 
		}
		//CREATE A ASSOCIATION

		//Following the same principles that we used to locate the Marketing folder, 
		//we can retrieve all children of the document library and look for the right folder. 
		//In this case, we assume that this folder exists.
		Folder whitepaperFolder = null; 
		for (CmisObject child :documentLibrary.getChildren()) { 
		   if ("Whitepapers".equals(child.getName())) { 
		         whitepaperFolder = (Folder) child; 
		   } 
		}
		
		// look for a whitepaper 
		Document whitepaper = null; 
		for (CmisObject child :whitepaperFolder.getChildren()) { 
		   if (child.getType().getId().equals("D:sc:whitepaper")) 
		         whitepaper = (Document) child; 
		} 
		
		//And finally, you can create the association:
		try{
		properties = new HashMap<String, Object>(); 
		properties.put(PropertyIds.NAME, "a new relationship"); 
		properties.put(PropertyIds.OBJECT_TYPE_ID, "R:sc:relatedDocuments"); 
		properties.put(PropertyIds.SOURCE_ID, marketingDocument.getId()); 
		properties.put(PropertyIds.TARGET_ID, whitepaper.getId()); 		
		session.createRelationship(properties); 
		}catch(org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException e){
			System.out.println("The association already exists");
		}
		
		searchDocuments(session);
		//deleteDocuments(session);



	}
	
	private static void searchDocuments(Session session) {
		ItemIterable<QueryResult> results = session.query("SELECT * FROM sc:doc", false); 
		
		for (QueryResult thit : results) { 
		   for (PropertyData<?>property : thit.getProperties()) { 
		         String queryName = property.getQueryName(); 
		         Object value = property.getFirstValue(); 
		         System.out.println(queryName + ": " + value); 
		   } 
		   System.out.println("--------------------------------------"); 
		} 


	} 
	
	private static void deleteDocuments(Session session) { 
		String path = "/Sites/marketing/documentLibrary"; 
		Folder documentLibrary = (Folder) session.getObjectByPath(path); 
		
		Folder marketingFolder = null; 
		for (CmisObject child :documentLibrary.getChildren()) { 
		   if ("Marketing".equals(child.getName())) { 
		         marketingFolder = (Folder) child; 
		   } 
		} 
		
		if (marketingFolder != null) { 
		   for (CmisObject child :marketingFolder.getChildren()) { 
		         session.delete(child); 
		   } 
		} 
			 



	} 


}
