package it.abd.utility.test.wsdl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * Commands: xjc -wsdl <wsdlurl> -d 
 *
 */
public class Wsdl2JavaWithJCE {
	
	public Wsdl2JavaWithJCE(){}
	
	//Servizi Web per l'invio
	
    private static List<String> webServicesSip = new ArrayList<String>(Arrays.asList(
		    //https://condidoc-test.regione.toscana.it/IrisBusiness/business/soap/WSCoreEsitoSipService SERVIZI INVIO
		    //https://condidoc-test.regione.toscana.it/IrisBusiness/business/soap/WSCoreReceiveSipService SERVIZI STATUS
			
    		//"https://condidoc-test.regione.toscana.it/IrisBusiness/business/soap/ServiceSoap?wsdl",
			"https://condidoc-test.regione.toscana.it/IrisBusiness/business/soap/WSCoreReceiveSipService?wsdl",
			//"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreReceiveSipFromGateService?wsdl",
			//"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreManifestSipService?wsdl",
			//"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreSendSipService?wsdl",
			"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreEsitoSipService?wsdl"
			//"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreDIPSincroService?wsdl",
			//"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreDIPGetService?wsdl",
			//"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreDIPAsincroService?wsdl",
			//"https://condidoc-test.regione.toscana.it:443/IrisBusiness/business/soap/WSCoreDIPAsincroFromGateService?wsdl"	
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
	        /*
	        System.setProperty("http.proxySet", "true");
	        System.setProperty("http.proxyHost", "192.168.1.188");
	        System.setProperty("http.proxyPort", "3128");
	        System.setProperty("https.proxyHost", "192.168.1.188");
	        System.setProperty("https.proxyPort", "3128");
	        */
	        System.setProperty("javax.net.debug","all");
	        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation","true");
	        System.setProperty("sun.security.ssl.allowLegacyHelloMessages","true");
	        //System.setProperty("soapui.https.protocols=TLSv1.1,TLSv1.2
	        System.setProperty("https.protocols", "TLSv1.1,TLSv1.2");
	        
	        
	        
	        for(String url : webServicesSip){
		        //With cxf
		        try {       	
		        	URI uri = new URI(url);
		        	String[] segments = uri.getPath().split("/");
		        	String idStr = segments[segments.length-1];
		        	idStr = idStr.replace("?wsdl", "");
		        	/*
			        JavaToWS.main(new String[] { "-d", "src", "-o", "ChangeStudent.wsdl", "-createxsdimports", "-wsdl",
			            "com.student.ChangeStudentDetailsImpl" });
			        System.out.println("finished %%%%%%%%%%");
			        */
		        	
		        	//Convertiemo url to a local file
		        	String pathFile = "wsdl/dax/"+idStr+".wsdl";
					ClassLoader classLoader = new Wsdl2JavaWithJCE().getClass().getClassLoader();
	                url = classLoader.getResource(pathFile).toString();
		        	//ClassLoader classLoader = Wsdl2JavaWithJCE.class.getClassLoader();
		        	//File file = new File(classLoader.getResource(pathFile).getFile());
		        	//url = file.toURI().toURL().toString();
		        	
		        	String[] myargs = new String[] { 
		        			
		        	//xjc -wsdl "https://condidoc-test.regione.toscana.it/IrisBusiness/business/soap/ServiceSoap?wsdl" -p "it.abd.alfresco.conservazione.dax2.wsdl.ServiceSoap" -d "C:\Users\Pancio\Desktop" -client -server
		        			"xjc",
		        			"-wsdl",
		        			url ,
		        			"-p",
		        			"it.abd.alfresco.conservazione.dax2.wsdl." + idStr,
		        			"-d",
		        			"C:\\Users\\Pancio\\Desktop",
		        			"-client", //generate starting point for a client maniline
		        			"-server",
		        			//"-impl",
		        			//"-exsh false",
		        			//"-dns true",
		        			//"-dex true",	        			
					};
		        	
		        	 try {		
	        		   String s = null;
	        		    // run the Unix "ps -ef" command
        	            // using the Runtime exec method:
        	            Process p = Runtime.getRuntime().exec(myargs);	        	            
        	            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        	            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        	            // read the output from the command
        	            //System.out.println("Here is the standard output of the command:\n");
        	            while ((s = stdInput.readLine()) != null) {
        	                System.out.println(s);
        	            }
        	            
        	            if((s = stdError.readLine()) != null){
	        	            // read any errors from the attempted command
	        	            System.out.println("Here is the standard error of the command (if any):\n");
	        	            while ((s = stdError.readLine()) != null) {
	        	                System.out.println(s);
	        	            }
        	            }
        	            
        	            //System.exit(0);
        	        }
        	        catch (IOException e) {
        	            System.out.println("exception happened - here's what I know: ");
        	            e.printStackTrace();
        	            System.exit(-1);
        	        }
	        	    		        	
			      } catch (Exception e) {
			        e.printStackTrace();
			      }
		        System.out.println("finished " + url);
	        }
	          System.out.println("finished %%%%%%%%%%");
	    }		
	
}
