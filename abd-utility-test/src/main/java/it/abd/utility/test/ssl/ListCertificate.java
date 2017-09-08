package it.abd.utility.test.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.map.HashedMap;

/**
 * 
 *
 */
public class ListCertificate {
	
	public static void main(String args[]) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		String pathKeyStore = args[0];
		String password = args[1];
		
		listCertificates(pathKeyStore,password);
	}
	
	/**
	 * /usr/java/default/bin/keytool -list -keystore cacerts | grep "," | sort | awk -F, {'print $1'}
	 *
	 */
	public static void listCertificates(String pathKeyStore,String password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(new FileInputStream(pathKeyStore), password.toCharArray());
        Enumeration<String> aliases = keystore.aliases();
        Map<String,String> map = new HashedMap();
        while(aliases.hasMoreElements()){
            String alias = aliases.nextElement();
            if(keystore.getCertificate(alias).getType().equals("X.509")){
            	Date expired = ((X509Certificate) keystore.getCertificate(alias)).getNotAfter();
//            	X509Certificate myCert = (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(// string encoded with default charset
//                       new ByteArrayInputStream(cert.getBytes())
//                        );
                //System.out.println(alias + " expires " + ((X509Certificate) keystore.getCertificate(alias)).getNotAfter());
                map.put(alias, expired.toString());                
            }
    	}
        Map<String, String> treeMap = new TreeMap<String, String>(map);
        for (Map.Entry entry : treeMap.entrySet()) {
            System.out.println(entry.getKey() + " expires " + entry.getValue());
        }
	}
	
	
	/*
	public static void getInfoSignPDF(File filePdf){
				
		Provider provider = null;
        try {
            Class c =
            Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            java.security.Security.insertProviderAt((java.security.Provider)c.newInstance(), 2000);
            //provider = "BC";
            provider = (Provider)c.newInstance();
            
        } catch(Exception e) {
            provider = null;
               // provider is not available }
        }		
		
		InputStream is = new FileInputStream(filePdf);
		
		PdfReader reader = new PdfReader(is);
        AcroFields af = reader.getAcroFields();
        ArrayList<String> names = af.getSignatureNames();
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        List<Map<QName, Serializable>> aspects = new ArrayList<Map<QName, Serializable>>();
        for (String name : names) {
            PdfPKCS7 pk = af.verifySignature(name);
            X509Certificate certificate = pk.getSigningCertificate();
            
            //Set aspect properties for each signature
            Map<QName, Serializable> aspectSignatureProperties = new HashMap<QName, Serializable>(); 
            if (pk.getSignDate() != null) aspectSignatureProperties.put(SignModel.PROP_DATE, pk.getSignDate().getTime());
    		aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_PRINCIPAL, certificate.getSubjectX500Principal().toString());
    	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_SERIAL_NUMBER, certificate.getSerialNumber().toString());
    	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_NOT_AFTER, certificate.getNotAfter());
    	    aspectSignatureProperties.put(SignModel.PROP_CERTIFICATE_ISSUER, certificate.getIssuerX500Principal().toString());   
    	    aspects.add(aspectSignatureProperties);
        }
        
        System.out.println(aspects);
	}
	*/

}
