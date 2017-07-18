package com.optaros.cmis;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.apache.chemistry.CMISObject;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.ObjectId;
import org.apache.chemistry.Repository;
import org.apache.chemistry.RepositoryInfo;
import org.apache.chemistry.Type;
import org.apache.chemistry.atompub.client.APPConnection;
import org.apache.chemistry.atompub.client.ContentManager;
import org.apache.chemistry.atompub.client.connector.APPContentManager;

public class ChemistryTest extends TestCase {
	private static final String USER = "admin";
	private static final String PASS = "admin";
	//private static final String CMIS_REPO_URL = "http://localhost:8080/cmis/repository";
	private static final String CMIS_REPO_URL = "http://localhost:8080/alfresco/s/cmis";

	public void testGetRepositoryInfo() {
		ContentManager contentManager = new APPContentManager(CMIS_REPO_URL);
		contentManager.login(USER, PASS);
		Repository repository = contentManager.getDefaultRepository();
		RepositoryInfo repositoryInfo = repository.getInfo();
		System.out.println("Product:" + repositoryInfo.getProductName());
		System.out.println("Vendor:" + repositoryInfo.getVendorName());
		System.out.println("Version:" + repositoryInfo.getVersionSupported());		
	}
	
	public void testGetTypes() {
		ContentManager contentManager = new APPContentManager(CMIS_REPO_URL);
		contentManager.login(USER, PASS);
		Repository repository = contentManager.getDefaultRepository();
		Collection<Type> types = repository.getTypes("cmis:document");
		for (Type type : types) {
			System.out.println("Type id:" + type.getId());
		}
	}
	
	public void testGetRootChildren() {
		APPContentManager contentManager = new APPContentManager(CMIS_REPO_URL);
		contentManager.login(USER, PASS);
		Repository repository = contentManager.getDefaultRepository();
		RepositoryInfo repositoryInfo = repository.getInfo();
		ObjectId rootObjId = repositoryInfo.getRootFolderId();
		APPConnection connection = (APPConnection) repository.getConnection(null);
		CMISObject rootObj = connection.getObject(rootObjId);
		System.out.println("Root object id:" + rootObj.getId());
		List<ObjectEntry> children = connection.getFolderTree(rootObjId, 1, null, false);
		for (ObjectEntry entry : children) {
			System.out.println(entry.getValue("cmis:name"));
		}
	}
}
