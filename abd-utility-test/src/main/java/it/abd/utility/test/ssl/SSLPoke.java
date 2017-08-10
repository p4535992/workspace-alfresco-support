package it.abd.utility.test.ssl;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.eclipse.jetty.http.HttpGenerator;

import java.io.*;

/** Establish a SSL connection to a host and port, writes a byte and
 * prints the response. See
 * http://confluence.atlassian.com/display/JIRA/Connecting+to+SSL+services
 * 
 * Test of java SSL / keystore / cert setup. Came from https://confluence.atlassian.com/download/attachments/117455/SSLPoke.java
 *
 *   Usage:
 *
 *   extract cert from server:
 *   openssl s_client -connect server:443
 *   negative test cert / keytool:
 *   java SSLPoke server 443
 *   you should get something like
 *   javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
 *   import cert into default keytool:
 *   keytool -import -alias alias.server.com -keystore $JAVA_HOME/jre/lib/security/cacerts
 *   positive test cert / keytool:
 *   java SSLPoke server 443
 *   you should get this:
 *   Successfully connected
 *
 */
public class SSLPoke {
    public static void main(String[] args) {
    	
    	args[0] = "https://condidoc-test.regione.toscana.it/IrisBusiness/";//"http://sip.receive.core.iris.eng.it";//"192.168.72.14";
    	args[1] = "443";//"5050";
    	
        if (args.length != 2) {
            System.out.println("Usage: "+SSLPoke.class.getName()+" <host> <port>");
            System.exit(1);
        }
        try {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(args[0], Integer.parseInt(args[1]));

            InputStream in = sslsocket.getInputStream();
            OutputStream out = sslsocket.getOutputStream();

            // Write a test byte to get a reaction :)
            out.write(1);

            while (in.available() > 0) {
                System.out.print(in.read());
            }
            System.out.println("Successfully connected");
            
            
//            String url = ESB_ADDRESS+"SELECT+*+FROM+"+ TABELLA_PAZIENTI +"+where+RIC_ANNO='"+annoNosografico+"'+AND+ANNO_PROG='"+progressivoNosografico+"'"+andWhere;
//            System.out.println("Url = "+url);
//			HttpGenerator method = new HttpGet(url);
//			CloseableHttpResponse resp;
//			try{
//				resp = client.execute(method);
//			}catch(IOException ioe){
//				throw new WebdesktopException(ioe.getMessage());
//			}

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}