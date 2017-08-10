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
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Classe per installare un file fisico di certificato sulla macchina jvm
 * NOTA: devi far corre la classe come amministratore su windows
 * e.g. keytool -import -alias dax -file "C:\Users\Pancio\Desktop\OLD CERT\condidoc-testregionetoscanait.crt" -keystore "C:\Program Files\Java\jdk1.7.0_55\jre\lib\security\cacerts"
 * e.g. "C:\Users\Pancio\Desktop\OLD CERT\condidoc-testregionetoscanait.crt" dax2 changeit "C:\Program Files\Java\jdk1.7.0_55\jre\lib\security\cacerts"
 * e.g. "C:\Users\Pancio\Desktop\condidoc-testregionetoscanait.crt" 
 */
public class InstallCertPhisicalFile {

    public static void main(String[] args) throws Exception {
    	
    	System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxyHost", "192.168.1.188");
        System.setProperty("http.proxyPort", "3128");
        System.setProperty("https.proxyHost", "192.168.1.188");
        System.setProperty("https.proxyPort", "3128");
        
        if(args.length == 4){ 
            String certfile = args[0];//"yourcert.cer"; /*your cert path*/
            String alias = args[1];//"youralias";
            String passphrase = args[2];//yourKeyStorePass";
            String pathKeyStore = args[3];
	        FileInputStream is = new FileInputStream(pathKeyStore);//yourKeyStore.keystore

	        char[] password = passphrase.toCharArray();
	
	        CertificateFactory cf = CertificateFactory.getInstance("X.509");
	        InputStream certstream = fullStream (certfile);
	        Certificate certs =  cf.generateCertificate(certstream);
   
	        File file = new File("jssecacerts");
	        if (file.isFile() == false) {
	            char SEP = File.separatorChar;
	            File dir;
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
	        System.out.println("Loading KeyStore " + file + "...");
	        
	         //KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	        //keystore.load(is, passphrase.toCharArray());
	        
	        //File keystoreFile = new File(pathKeyStore); //"yourKeyStorePass.keystore"
	        ///FileInputStream in = new FileInputStream(keystoreFile);
	        //keystore.load(in, password);
	        //in.close();
      
	        InputStream in = new FileInputStream(file);
	        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			//ks.load(new FileInputStream( certPath ), certPasswd.toCharArray() );
	        ks.load(in, password);
	        in.close();
	        
	        
	        // Save the new keystore contents

	
	        // Add the certificate
	        Certificate certificate = ks.getCertificate(alias);
	        ks.setCertificateEntry(alias, certs);	        
	        Key key = ks.getKey(alias, password);
	        
	        if(file.exists()){
	        
	        listCertificate(file,passphrase);

	        OutputStream out = new FileOutputStream(file);
	        ks.store(out, password);
	        out.close();
	        
	        X509Certificate certX05 = (X509Certificate) certs;
	        String dn = certX05.getSubjectX500Principal().getName();
	        LdapName ldapDN = new LdapName(dn);
	        for(Rdn rdn: ldapDN.getRdns()) {
	            System.out.println("DN:" + rdn.getType() + " -> " + rdn.getValue());
	        }
	    
	        System.out.println("The phisical certificate <" + certfile +"> is been importd in the keystore <"+ pathKeyStore + "> with alias <" + alias + ">\r\n");
	        }
        } else {
            System.out.println("Usage: java InstallCertPhisicalFile [pathCertFile] [alias] [passStore] [filePathKeystore] ");
            return;
        }

    }

    private static InputStream fullStream ( String fname ) throws IOException {
        FileInputStream fis = new FileInputStream(fname);
        DataInputStream dis = new DataInputStream(fis);
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return bais;
    }
    
    //https://gist.github.com/stevebradshaw/4633000
    @SuppressWarnings("unchecked")
	private static void listCertificate(File pathKeyStore, String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
    	KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(new FileInputStream(pathKeyStore), password.toCharArray());
        Enumeration<String> aliases = keystore.aliases();
        List<String> list= Collections.list(aliases); // create list from enumeration 
        Collections.sort(list);
        aliases = Collections.enumeration(list);       
        while(aliases.hasMoreElements()){
            String alias = aliases.nextElement();
            if(keystore.getCertificate(alias).getType().equals("X.509")){
//            	X509Certificate myCert = (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(// string encoded with default charset
//                       new ByteArrayInputStream(cert.getBytes())
//                        );
                System.out.println(alias + " expires " + ((X509Certificate) keystore.getCertificate(alias)).getNotAfter());
            }
    	}
    }

    
}