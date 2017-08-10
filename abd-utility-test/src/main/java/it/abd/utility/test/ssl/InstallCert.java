package it.abd.utility.test.ssl;



/*
 * Copyright 2006 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * Originally from:
 * http://blogs.sun.com/andreas/resource/InstallCert.java
 * Use:
 * java InstallCert hostname
 * Example:
 *% java InstallCert ecc.fedora.redhat.com
 */

import javax.net.ssl.*;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.SocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Class used to add the server's certificate to the KeyStore
 * with your trusted certificates.
 * USAGE: <hostname>:<port> <password_keystore> <path_to_keystore> 
 * e.g. 159.213.238.75:443 changeit 
 * e.g. InstallCert 192.168.72.87:5050 changeit /opt/alfresco-4.2.f/java/jre/lib/security/cacerts
 * e.g. condidoc-test.regione.toscana.it:443 changeit C:\Program Files\Java\jdk1.7.0_55\jre\lib\security\cacerts
 * e.g. /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.65-2.b17.el7_1.x86_64/jre/bin/java InstallCert arss.abd.it:443 changeit /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.65-2.b17.el7_1.x86_64/jre/lib/security/cacerts
 * e.g. repo.boundlessgeo.com:443 changeit C:\Program Files\Java\jdk1.7.0_55\jre\lib\security\cacerts
 * e.g. /usr/local/jdk1.8.0_60/jre/bin/java InstallCert arss.abd.it:443 password testInternoTestKeystore.jks
 */
public class InstallCert {

    public static void main(String[] args) throws Exception {
    	
    	System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxyHost", "192.168.1.188");
        System.setProperty("http.proxyPort", "3128");
        System.setProperty("https.proxyHost", "192.168.1.188");
        System.setProperty("https.proxyPort", "3128");
    	
    	String host;
        int port;
		String pathKeyStore;
        char[] passphrase;
        if ((args.length == 1) || (args.length == 2) || (args.length == 3)) {
            String[] c = args[0].split(":");
            host = c[0];
            port = (c.length == 1) ? 443 : Integer.parseInt(c[1]);
            String p = (args.length == 1) ? "changeit" : args[1];
            passphrase = p.toCharArray();
            
            if(args.length >= 3){
            	pathKeyStore = args[2];
            }else{
            	pathKeyStore = System.getProperty("java.home");
            }
            
            
            
    		System.out.println("HOST:" + host + ",PORT:"+ port + ",PASSPHRASE:" + passphrase.toString() + ",PATHKEYSTORE:" + pathKeyStore);
        } else {
            System.out.println("Usage: java InstallCert <host>[:port] [passphrase] [path to security folder]");
            return;
        }
		
        File file = new File("jssecacerts");        
        if (file.isFile() == false) {
            char SEP = File.separatorChar;
            File dir;
            
            if(pathKeyStore.toLowerCase().trim().endsWith("jks")){
            	file = new File(pathKeyStore);           
	            if (file.isFile() == false) {
                	throw new IOException("Not exists any keystore JKS of default under the keystore path:" + pathKeyStore);
	            }
            }else{
	            //if path is under XXX/lib/security/ do set
	            if(pathKeyStore.contains(SEP + "lib" + SEP + "security")){
	            	dir = new File(pathKeyStore);	            	
	            }else{
	            	dir = new File(pathKeyStore + SEP + "lib" + SEP + "security");
	            }
	            //if path is under XXX/lib/security/jssecacerts or XXX/lib/security/cacerts do set
	            if(pathKeyStore.contains(SEP + "jssecacerts") || pathKeyStore.contains(SEP + "cacerts")){
	            	file = dir;
	            }else{
		            file = new File(dir, "jssecacerts");
		            if (file.isFile() == false) {
		                file = new File(dir, "cacerts");
		                if (file.isFile() == false){
		                	throw new IOException("Not exists any keystore of default 'jssecacerts' or 'cacerts' under the keystore path:" + pathKeyStore);
		                }
		            }
	            }
            }
        }
        System.out.println("Loading KeyStore " + file + "...");
        InputStream in = new FileInputStream(file);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		//ks.load(new FileInputStream( certPath ), certPasswd.toCharArray() );
        ks.load(in, passphrase);
        in.close();

        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory factory = context.getSocketFactory();
        
        
        InetAddress address = InetAddress.getByName(host);

        System.out.println("Opening connection to " + host + ":" + port + " with InetAdress: "+address.toString()+" ...");
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setSoTimeout(10000);
        try {
            System.out.println("Starting SSL handshake...");
            socket.startHandshake();
            socket.close();
            System.out.println();
            System.out.println("No errors, certificate is already trusted");
        } catch (SSLException e) {
            System.out.println();
            e.printStackTrace(System.out);
        }

        X509Certificate[] chain = tm.chain;
        if (chain == null) {
            System.out.println("Could not obtain server certificate chain");
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println();
        System.out.println("Server sent " + chain.length + " certificate(s):");
        System.out.println();
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        for (int i = 0; i < chain.length; i++) {
            X509Certificate cert = chain[i];
            System.out.println(" " + (i + 1) + " Subject " + cert.getSubjectDN());
            System.out.println("   Issuer  " + cert.getIssuerDN());
            sha1.update(cert.getEncoded());
            System.out.println("   sha1    " + toHexString(sha1.digest()));
            md5.update(cert.getEncoded());
            System.out.println("   md5     " + toHexString(md5.digest()));
            System.out.println();
        }

        System.out.println("Enter certificate to add to trusted keystore or 'q' to quit: [1]");
        String line = reader.readLine().trim();
        int k;
        try {
            k = (line.length() == 0) ? 0 : Integer.parseInt(line) - 1;
        } catch (NumberFormatException e) {
            System.out.println("KeyStore not changed");
            return;
        }

        X509Certificate cert = chain[k];        
        String alias = host + "-" + (k + 1);
        ks.setCertificateEntry(alias, cert);

        OutputStream out = new FileOutputStream(file);
        ks.store(out, passphrase);
        out.close();

        System.out.println();
        System.out.println(cert);
        System.out.println();
        System.out.println("Added certificate to keystore '"+pathKeyStore+"' using alias '"+ alias + "'");
        
        System.out.println("Other infromation");
        
        //List<Collection<List<?>>> asList = Arrays.asList(cert.getSubjectAlternativeNames());
		//System.out.println("List of Hostnames:" + asList.toArray().toString());
        
        //System.out.println("List of Hostnames:" + getSubjectAlternativeNames(cert));
        
//        x500name x500name = new CertificateHolder(cert).getSubject();
//        RDN cn = x500name.getRDNs(BCStyle.CN)[0];
//        IETFUtils.valueToString(cn.getFirst().getValue());
        
        //Print the ldap information
        String dn = cert.getSubjectX500Principal().getName();
        LdapName ldapDN = new LdapName(dn);
        for(Rdn rdn: ldapDN.getRdns()) {
            System.out.println("DN:" + rdn.getType() + " -> " + rdn.getValue());
        }
		System.out.println("My Hostnames:" +InetAddress.getByName(host).getHostName() + " or " + InetAddress.getByName(host).getCanonicalHostName()); 

    }

    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }

    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {       
	        /* 
	         * This change has been done due to the following resolution advised for Java 1.7+
			 * http://infposs.blogspot.kr/2013/06/installcert-and-java-7.html
	         */ 
        	return new X509Certificate[0];	
            //throw new UnsupportedOperationException();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }
    
    /**
     * Using the following code it is possible to add a trust store during runtime.
     * @param trustStore
     * @param password
     * @throws Exception
     */
    public static void setTrustStore(String trustStore, String password) throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream keystoreStream = InstallCert.class.getResourceAsStream(trustStore);
        keystore.load(keystoreStream, password.toCharArray());
        trustManagerFactory.init(keystore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustManagers, null);
        SSLContext.setDefault(sc);
    }
    

    
    
}