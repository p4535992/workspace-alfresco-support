package it.abd.utility.test.wsdl;

import java.io.File;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import org.apache.cxf.tools.wsdlto.WSDLToJava;

public class Wsdl2JavaWithCXF {
	
	//Servizi Web per l'invio
	
	private static List<String> webServicesSip = new ArrayList<String>(Arrays.asList(
	    //https://condidoc-test.regione.toscana.it/IrisBusiness/business/soap/WSCoreEsitoSipService SERVIZI INVIO
	    //https://condidoc-test.regione.toscana.it/IrisBusiness/business/soap/WSCoreReceiveSipService SERVIZI STATUS
//		
//		"https://condidoc-test.regione.toscana.it/IrisBusiness/business/soap/ServiceSoap?wsdl",
//		"https://condidoc-test.regione.toscana.it/IrisBusiness/business/soap/WSCoreReceiveSipService?wsdl",
//		"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreReceiveSipFromGateService?wsdl",
//		"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreManifestSipService?wsdl",
//		"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreSendSipService?wsdl",
//		"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreEsitoSipService?wsdl",
//		"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreDIPSincroService?wsdl",
//		"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreDIPGetService?wsdl",
//		"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreDIPAsincroService?wsdl",
//		"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreDIPAsincroFromGateService?wsdl"
			
		//"file:///C:/Users/Pancio/Desktop/test_wsdl/wsdl/ArubaSignService.wsdl"
		//"file:///C:/Users/Pancio/Desktop/test_wsdl/wsdl/ArubaSignService-v1.13.2.wsdl"
	    //"file:///C:/Users/Pancio/Desktop/test_wsdl/wsdl/ArubaSignService-v1.13.7.wsdl"
		"file:///C:/workspace-eclipse-2017/workspace-alfresco-support/abd-utility-test/src/main/resources/wsdl/FirmaRemotaService.wsdl"
	));


	
	
	public static void main(String... args) throws Exception {
		
		//System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
		//System.setProperty("javax.net.ssl.keyStore", "PRIVATEKEY.p12");
		//System.setProperty("javax.net.ssl.keyStorePassword", "PASSWORD"); 
		
        SSLTruster.trustAll();
        //With axis 2
        //org.apache.axis2.wsdl.WSDL2Java.main(args);
        
        //Client cl = ClientProxy.getClient("https://condidoc-test.regione.toscana.it/IrisBusiness/business/soap/ServiceSoap?wsdl");
        //HTTPConduit httpConduit = (HTTPConduit) cl.getConduit();
        
        /*
        org.apache.cxf.endpoint.Client client = ClientProxy.getClient(null);
        HTTPConduit http = (HTTPConduit) client.getConduit();
        http.getClient().setProxyServer("192.168.1.188");
        http.getClient().setProxyServerPort(3128);
        http.getProxyAuthorization().setUserName("root");
        http.getProxyAuthorization().setPassword("abd2016");
        
        
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(30000); //Time in milliseconds
        httpClientPolicy.setReceiveTimeout(30000); //Time in milliseconds
        //httpConduit.setClient(httpClientPolicy);
        */
        
        //Settiamo le variabili per il proxy abd
        System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxyHost", "192.168.1.188");
        System.setProperty("http.proxyPort", "3128");
        System.setProperty("https.proxyHost", "192.168.1.188");
        System.setProperty("https.proxyPort", "3128");
        
        
        
        for(String url : webServicesSip){
	        //With cxf
	        try {       	
	        	URI uri = new URI(url);
	        	File f = new File(uri);
	        	String[] segments = uri.getPath().split("/");
	        	String idStr = segments[segments.length-1];
	        	idStr = idStr.replace("?wsdl", "");
	        	/*
		        JavaToWS.main(new String[] { "-d", "src", "-o", "ChangeStudent.wsdl", "-createxsdimports", "-wsdl",
		            "com.student.ChangeStudentDetailsImpl" });
		        System.out.println("finished %%%%%%%%%%");
		        */
	        	String[] myargs = new String[] { 
	        			
	        			"-p",
	        			//"it.abd.alfresco.conservazione.sip.wsdl." + idStr,
	        			//"it.arubapec.arubasignservice.i.xiii.vii",
	        			"it.abd.esb.firmaremota",
	        			"-d",
	        			"C:\\Users\\Pancio\\Desktop\\test_wsdl",
	        			"-client", //generate starting point for a client maniline
	        			"-server",
	        			"-impl",
	        			"-wsdlLocation",
	        			"classpath:wsdl"+"/"+f.getName(),
	        			//"-exsh false",
	        			//"-dns true",
	        			//"-dex true",
	        			"-verbose",
	        			url	        			
				};
	        	System.out.println(Arrays.toString(myargs));
	        	WSDLToJava.main(myargs);
		      } catch (Exception e) {
		        e.printStackTrace();
		      }
	        System.out.println("finished " + url);
        }
          System.out.println("finished %%%%%%%%%%");
    }
}


class SSLTruster {
    public static void trustAll(){
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {		
			 public boolean verify(String hostname, SSLSession session) {
               return true;
           }
			
		});
      try {
        SSLContext context = SSLContext.getInstance("SSL");
          context.init(null,
                new TrustManager[]{new X509TrustManager() {
					
					public X509Certificate[] getAcceptedIssuers() {
						return new X509Certificate[]{};
					}
					
					public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						// TODO Auto-generated method stub
						
					}
					
					public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						// TODO Auto-generated method stub
						
					}
					
				}},new SecureRandom());
          
          HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (GeneralSecurityException gse) {
            throw new IllegalStateException(gse.getMessage());
        }
    }
}