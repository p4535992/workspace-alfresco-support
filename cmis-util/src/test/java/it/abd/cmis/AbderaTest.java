package it.abd.cmis;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.TestCase;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;

import sun.misc.BASE64Encoder;

public class AbderaTest extends TestCase {
	public static final String CMIS = "cmis";
	public static final String CMISRA = "cmisra";
	public static final String NS_CMIS_CORE = "http://docs.oasis-open.org/ns/cmis/core/200908/";
	public static final String NS_CMIS_RESTATOM = "http://docs.oasis-open.org/ns/cmis/restatom/200908/";
	
	private static Abdera abdera = null;
    
    public static synchronized Abdera getAbderaInstance() {
      if (abdera == null) abdera = new Abdera();
      return abdera;
    }

    public static String getUserNamePassword() {
    	return "admin:admin";
    }
    
	public void testGet() {
		Abdera abdera = AbderaTest.getAbderaInstance();
		
		Parser parser = abdera.getParser();
		
		try {
			URL url = new URL("http://localhost:8080/alfresco/s/cmis/p/Someco/children");
			String encoding = new BASE64Encoder().encode(getUserNamePassword().getBytes());
			URLConnection uc = url.openConnection();
			uc.setRequestProperty ("Authorization", "Basic " + encoding);	
			InputStream content = (InputStream)uc.getInputStream();
			Document<Feed> doc = parser.parse(content);
			Feed feed = doc.getRoot();
			System.out.println(feed.getTitle());
			for (Entry entry : feed.getEntries()) {
			   System.out.println("\t" + entry.getTitle());
			}
			assertTrue(feed.getEntries().size() > 0);
		} catch (MalformedURLException mue) {
			mue.printStackTrace();		
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testCreateFolder() {
				
		Abdera abdera = AbderaTest.getAbderaInstance();
	
		// Build the entry
		Entry entry = abdera.newEntry();
		entry.setTitle("test3");
		entry.setSummary("test folder 3");
		
		ExtensibleElement objElement = (ExtensibleElement) entry.addExtension(NS_CMIS_RESTATOM, "object", CMISRA);
		ExtensibleElement propsElement = objElement.addExtension(NS_CMIS_CORE, "properties", CMIS);
		ExtensibleElement stringElement = propsElement.addExtension(NS_CMIS_CORE, "propertyId", CMIS);
		stringElement.setAttributeValue("propertyDefinitionId", "cmis:objectTypeId");
		Element valueElement = stringElement.addExtension(NS_CMIS_CORE, "value", CMIS);
		valueElement.setText("cmis:folder");
		
		// Post it
		AbderaClient client = new AbderaClient();
		String encoding = new BASE64Encoder().encode("admin:admin".getBytes());			
		RequestOptions options = new RequestOptions();
		options.setHeader("Authorization", "Basic " + encoding);
		ClientResponse response = null;
		response = client.post("http://localhost:8080/alfresco/s/cmis/p/Someco/children", entry, options);
		
		assertEquals(201, response.getStatus());
	}
}
