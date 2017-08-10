package it.abd.utility.test.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;

public class RemoveCertificate {
	
	public static void main(String args[]) throws IOException{

		String password = args[0];
		String alias = args[1];
		String pathKeyStore = "";
		
		if(args.length >= 3){
            pathKeyStore = args[2];
        }else{
         	pathKeyStore = System.getProperty("java.home");
        }
		
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
	    try {
	    	FileInputStream fis = new FileInputStream(file);
	    
	        final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	        keystore.load(fis, password.toCharArray());
	        if (keystore.containsAlias(alias)) {
	            keystore.deleteEntry(alias);
	        }
	        else {
	            throw new IllegalStateException("Alias " + alias + " not found in trust store");
	        }
	        //Overwrite old keystore
	        OutputStream writeStream = new FileOutputStream(file);
	        keystore.store(writeStream, password.toCharArray());
	        writeStream.close();
	        
	        fis.close();
	    }
	    catch (final Exception e) {
	        throw new IllegalStateException("Error occures while deleting certificate.", e);
	    }
	    
	    
	    
	    System.out.println("Deleted certificate with alias <"+alias+"> on the  KeyStore <" + file + "> ");		
	}

}
