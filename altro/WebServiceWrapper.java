package it.abd.alfresco.conservazione.dax.xsd.classi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.springframework.http.HttpMethod;

import it.abd.alfresco.conservazione.dax.wsdl.WSCoreReceiveSipService.ObjectFactory;
import it.abd.alfresco.conservazione.dax.wsdl.WSCoreReceiveSipService.ReceiveSipResponse;

import java.util.Map;

import javax.print.attribute.standard.ReferenceUriSchemesSupported;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

/**
 * webServiceWrapper = new WebServiceWrapper("10.68.66.170", "admin", "<some password>");
 * webServiceWrapper.invokeWebService("ValidationService", "validateConfiguration");
 *
 */
public class WebServiceWrapper {

    private static final String TIMEOUT_MS = "120000";

    private JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();

    private String hostIp;
    private String wsdlUrl;

    private String userName;

    private String password;

    private Map<String, CachedClient> clientCache = new HashMap<String, CachedClient>();

    public WebServiceWrapper(String hostIp,String wsdlUrl, String userName, String password) {
        this.hostIp = hostIp;
    	this.wsdlUrl = wsdlUrl;
        this.userName = userName;
        this.password = password;
    }
    
    public WebServiceWrapper(String hostIp, String userName, String password) {
        this.hostIp = hostIp;   	
        this.userName = userName;
        this.password = password;
    }

    public Object[] invokeWebService(String serviceName, String operationName, Object... params) throws Exception {
    	ReferenceUriSchemesSupported protocol = ReferenceUriSchemesSupported.HTTPS;
    	String sProtocol = protocol.toString().toLowerCase();
        ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
        
        //String wsdlRef = "http://" + hostIp + "/wfa-ws/" + serviceName + "?wsdl";     
        this.wsdlUrl = sProtocol + "://" + hostIp + "/" + serviceName + "?wsdl";
        CachedClient cachedClient = getClient(wsdlUrl,prevClassLoader);
        //CachedClient cachedClient = getClient(wsdlUrl);
        try {

            return cachedClient.client.invoke(operationName, params);

        } catch (Exception e) {

            throw new Exception("Web-service invocation failed invoking " + serviceName + "." + operationName, e);

        } finally {

            // Restore the original class loader

            Thread.currentThread().setContextClassLoader(prevClassLoader);

        }

    }

    @Override

    public String toString() {

        return "WebServiceWrapper{" +

                "hostIp='" + hostIp + '\'' +
                
                "wsdlUrl='" + wsdlUrl + '\'' +

                ", userName='" + userName + '\'' +

                ", password='" + password + '\'' +

                '}';

    }

    public String getHostIp() {

        return hostIp;

    }

    Map<String, CachedClient> getClientCache() {

        return Collections.unmodifiableMap(clientCache);

    }

    private CachedClient getClient(String wsdlUrl,ClassLoader jaxbContextLoader) throws JAXBException, MalformedURLException {
    	
    	it.abd.alfresco.conservazione.dax.wsdl.WSCoreReceiveSipService.ObjectFactory objectFactory = 
    			new it.abd.alfresco.conservazione.dax.wsdl.WSCoreReceiveSipService.ObjectFactory();
    	//JAXBContext jaxbContext = JAXBContext.newInstance(objectFactory.getClass());
    	
    	//ClassLoader jaxbContextLoader = it.abd.alfresco.conservazione.dax.wsdl.WSCoreReceiveSipService.ObjectFactory.class.getClassLoader();
    	//ClassLoader jaxbContextLoader = it.abd.alfresco.conservazione.dax.wsdl.WSCoreReceiveSipService.WSICoreReceiveSip.class.getClassLoader();
    	//ClassLoader jaxbContextLoader = Thread.currentThread().getContextClassLoader(); 
    	
    	javax.xml.namespace.QName serviceName = new javax.xml.namespace.QName("http://sip.receive.core.iris.eng.it","WSCoreReceiveSipService");	
    
    	
    	//javax.xml.namespace.QName serviceName = new QName("http://sip.receive.core.iris.eng.it", "receiveSip");
    	//javax.xml.namespace.QName portName = new QName("http://sip.receive.core.iris.eng.it", "receiveSipResponse");
    	//javax.xml.namespace.QName portName = new QName("http://sip.receive.core.iris.eng.it", "receiveSip");
    	//javax.xml.namespace.QName portName = new javax.xml.namespace.QName("http://sip.receive.core.iris.eng.it","WSCoreReceiveSipPortImpl");	
    	//javax.xml.namespace.QName portName = new javax.xml.namespace.QName("http://sip.receive.core.iris.eng.it", "WSCoreReceiveSip");
    	//javax.xml.namespace.QName portName = new javax.xml.namespace.QName("http://sip.receive.core.iris.eng.it", "WSICoreReceiveSip");
    	javax.xml.namespace.QName portName = new javax.xml.namespace.QName("http://sip.receive.core.iris.eng.it", "WSCoreReceiveSipPort");
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance(
    			it.abd.alfresco.conservazione.dax.wsdl.WSCoreReceiveSipService.ObjectFactory.class.getPackage().getName(),
    			jaxbContextLoader
    			);
    	
  
    	CachedClient cachedClient = clientCache.get(wsdlUrl);

        if (cachedClient == null) {
        	//Map<String, Object> jaxbContextProperties = new HashMap<String, Object>();
        	
        	//javax.xml.bind.Marshaller.
        	//jaxbContextProperties.put("com.sun.xml.bind.defaultNamespaceRemap", "uri:ultima:thule");        	
            //dcf.setJaxbContextProperties(jaxbContextProperties);
            URL url = new URL(wsdlUrl);
            Client client = dcf.createClient(wsdlUrl,serviceName,jaxbContextLoader,portName);
            //Client client = dcf.createClient(wsdlUrl,jaxbContextLoader);
            cachedClient = new CachedClient(dcf.createClient(url.toExternalForm()));

            cachedClient.classLoader = Thread.currentThread().getContextClassLoader();
            
            HTTPConduit httpConduit = (HTTPConduit) cachedClient.client.getConduit();

            AuthorizationPolicy authorization = httpConduit.getAuthorization();

            authorization.setUserName(userName);

            authorization.setPassword(password);

            cachedClient.client.getRequestContext().put("javax.xml.ws.client.receiveTimeout", TIMEOUT_MS);// 120 seconds

            cachedClient.client.getRequestContext().put("javax.xml.ws.client.connectionTimeout", TIMEOUT_MS);// 120 seconds

            clientCache.put(wsdlUrl, cachedClient);

        }

        else {

            // for existing client, restore's the client's class loader

            Thread.currentThread().setContextClassLoader(cachedClient.classLoader);

        }
        
        Marshaller marshaller = jaxbContext.createMarshaller();
    	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marshaller.marshal(ReceiveSipResponse.class, System.out);
    	

        return cachedClient;

    }

    public class CachedClient {

        Client client;

        ClassLoader classLoader;

        public CachedClient(Client client) {

            this.client = client;

        }

        public Client getClient() {

            return client;

        }

    }

}