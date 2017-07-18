package it.abd.cmis;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.RelationshipType;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.tck.atompub.http.Connection;

import junit.framework.TestCase;

//import org.apache.chemistry.CMISObject;
//import org.apache.chemistry.ObjectEntry;
//import org.apache.chemistry.ObjectId;
//import org.apache.chemistry.Repository;
//import org.apache.chemistry.RepositoryInfo;
//import org.apache.chemistry.Type;
//import org.apache.chemistry.atompub.client.APPConnection;
//import org.apache.chemistry.atompub.client.ContentManager;
//import org.apache.chemistry.atompub.client.connector.APPContentManager;

public class ChemistryTest extends TestCase {
	private static final String USER = "admin";
	private static final String PASS = "admin";
	//private static final String CMIS_REPO_URL = "http://localhost:8080/cmis/repository";
	private static final String CMIS_REPO_URL = "http://localhost:8080/alfresco/s/cmis";
	
	/**
	 * https://www.ibm.com/developerworks/community/blogs/e8206aad-10e2-4c49-b00c-fee572815374/entry/hellocmis_hellodocument_creating_folders_and_content?lang=en
	 */
	public void connectingNaviugatorOnCloud(){
		// default factory implementation
        SessionFactory factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();   
        // user credentials
        parameter.put(SessionParameter.USER, "p8admin");
        parameter.put(SessionParameter.PASSWORD, "filenet");   
        // connection settings
        parameter.put(SessionParameter.ATOMPUB_URL, "http://192.168.1.143:9080/fncmis/resources/Service");
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());        
        parameter.put(SessionParameter.REPOSITORY_ID, "ECM");
        // create session
        Session session = factory.createSession(parameter);
        List<Repository> repositories = factory.getRepositories(parameter);
        //CREATE A FOLDER
        
        //Get an instance of the root folder
  		Folder root = session.getRootFolder();

  		// properties
  		// (minimal set: name and object type id)
  		Map<String, Object> properties = new HashMap<String, Object>();
  		properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
  		properties.put(PropertyIds.NAME, "CMISDocuments");

  		// create the folder
  		Folder newFolder = root.createFolder(properties);
  		
  		//INSERT A DOCUMENT IN THE FOLDER
  		try {
  		    //create a document
  		    Map<String, Object> docProps = new HashMap<String, Object>();
  		    docProps.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
  		    docProps.put(PropertyIds.NAME, "the document name");
  		            
  		    File f = new File("C:\\Documents\\customerContactCenter.doc");
  		    InputStream isFile = new FileInputStream(f);
  		            
  		    ContentStream contentStream = session.getObjectFactory().createContentStream(f.getName(), f.length(), "application/ms-word", isFile);
  		            
  		    Document d = newFolder.createDocument(docProps, contentStream, VersioningState.MAJOR);
  		            
  		    System.out.println(d.getId());
  		    isFile.close();
  		} catch (Exception ex) {
  		    System.out.println("Something has gone horribly wrong.");
  		    ex.printStackTrace();
  		}
	}
	

	public void testGetRepositoryInfo() {
		//ContentManager contentManager = new APPContentManager(CMIS_REPO_URL);
		//contentManager.login(USER, PASS);
		//Repository repository = contentManager.getDefaultRepository();
		//RepositoryInfo repositoryInfo = repository.getInfo();
		//System.out.println("Product:" + repositoryInfo.getProductName());
		//System.out.println("Vendor:" + repositoryInfo.getVendorName());
		//System.out.println("Version:" + repositoryInfo.getVersionSupported());
		
		// Default factory implementation of client runtime.
		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		Map<String, String> parameter = new HashMap<String, String>();

		// User credentials.
		parameter.put(SessionParameter.USER, "user");
		parameter.put(SessionParameter.PASSWORD, "password");

		// Connection settings.
		parameter.put(SessionParameter.ATOMPUB_URL, "http://localhost:8080/alfresco/service/cmis"); // URL to your CMIS server.
		// parameter.put(SessionParameter.REPOSITORY_ID, "myRepository"); // Only necessary if there is more than one repository.
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		
		// Create session.
		Session session = null;
		try {
		    // This supposes only one repository is available at the URL.
		    Repository soleRepository = sessionFactory.getRepositories(parameter).get(0);
		    session = soleRepository.createSession();
		    
		    System.out.println("Product:" + soleRepository.getProductName());
			System.out.println("Vendor:" + soleRepository.getVendorName());
			System.out.println("Version:" + soleRepository.getCmisVersionSupported());	
		    
		}
		catch(CmisConnectionException e) { 
		    // The server is unreachable
		}
		catch(CmisRuntimeException e) {
		    // The user/password have probably been rejected by the server.
		}

	}
	
	public void testGetTypes() {
//		ContentManager contentManager = new APPContentManager(CMIS_REPO_URL);
//		contentManager.login(USER, PASS);
//		Repository repository = contentManager.getDefaultRepository();
		
		// default factory implementation
        SessionFactory factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();   
        // user credentials
        parameter.put(SessionParameter.USER, "p8admin");
        parameter.put(SessionParameter.PASSWORD, "filenet");   
        // connection settings
        parameter.put(SessionParameter.ATOMPUB_URL, "http://192.168.1.143:9080/fncmis/resources/Service");
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());        
        parameter.put(SessionParameter.REPOSITORY_ID, "ECM");
        // create session
        Session session = factory.createSession(parameter);
        List<Repository> repositories = factory.getRepositories(parameter);
		Repository repository = repositories.get(0);
		
//		Collection<Type> types = repository.getTypes("cmis:document");
//		for (Type type : types) {
//			System.out.println("Type id:" + type.getId());
//		}
		
		ObjectType type = session.getTypeDefinition("cmis:document");
		//Collection<ObjectType> types = session.
		if (type instanceof DocumentType) {
		    DocumentType docType = (DocumentType) type;
		    boolean isVersionable = docType.isVersionable();
		    System.out.println("Type id:" + docType.getId());
		} else if (type instanceof RelationshipType) {
		    RelationshipType relType = (RelationshipType) type;
		    System.out.println("Type id:" + relType.getId());
		} else {
		    //other cases
		}
//		
//		Collection<Type> types = repository.getTypes("cmis:document");
//		for (Type type : types) {
//			System.out.println("Type id:" + type.getId());
//		}

	}
	
	public void testGetRootChildren() {
//		APPContentManager contentManager = new APPContentManager(CMIS_REPO_URL);
//		contentManager.login(USER, PASS);
//		Repository repository = contentManager.getDefaultRepository();
		
		// default factory implementation
        SessionFactory factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();   
        // user credentials
        parameter.put(SessionParameter.USER, "p8admin");
        parameter.put(SessionParameter.PASSWORD, "filenet");   
        // connection settings
        parameter.put(SessionParameter.ATOMPUB_URL, "http://192.168.1.143:9080/fncmis/resources/Service");
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());        
        parameter.put(SessionParameter.REPOSITORY_ID, "ECM");
        // create session
        Session session = factory.createSession(parameter);
        List<Repository> repositories = factory.getRepositories(parameter);
		Repository repository = repositories.get(0);
		
		RepositoryInfo repositoryInfo = session.getRepositoryInfo();//repository.Info();
//		ObjectId rootObjId = repositoryInfo.getRootFolderId();
//		Connection connection = //repository.getConnection(null);
//		CMISObject rootObj = connection.getObject(rootObjId);
//		System.out.println("Root object id:" + rootObj.getId());
//		List<ObjectEntry> children = connection.getFolderTree(rootObjId, 1, null, false);
//		for (ObjectEntry entry : children) {
//			System.out.println(entry.getValue("cmis:name"));
//		}

		//Loop for all object under a folder
		Folder root = session.getRootFolder();
		for (Tree<FileableCmisObject> item : root.getDescendants(1)) {
            System.out.println("Itemname=" + item.getItem().getName());
            System.out.println("Itemtype=" + item.getItem().getType().getDisplayName());
            System.out.println("Itemtype=" + item.getItem().getPropertyValue("cmis:name"));
        }
	}
}
