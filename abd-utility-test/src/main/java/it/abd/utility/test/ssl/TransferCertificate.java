package it.abd.utility.test.ssl;

/**
 * Metodo per tansferire i certificati da un keystore ad un'altro
 * 
 * e.g."C:\Program Files\Java\jre7\bin\keytool.exe" -importkeystore -srckeystore "D:\source-keystore.jks" -destkeystore "D:\destination-keystore.jks" -srcstorepass password -deststorepass password -srcalias "www.mysecuresite.com"
 * e.g. /usr/java/default/bin/keytool -importkeystore -srckeystore  /usr/java/jdk1.7.0_71/jre/lib/security/cacerts -destkeystore /opt/apache-servicemix-6.0.0/keystore/livornoEsbKeystore.jks -srcstorepass changeit -deststorepass skpass -srcalias "srvesb.comuneli.local" 
 * e.g. keytool -importkeystore -srckeystore <source-encryption.keystore> -destkeystore <target-encryption.keystore>
 *
 */
public class TransferCertificate {
	
	///usr/java/default/bin/keytool -importkeystore -srckeystore  /usr/java/jdk1.7.0_71/jre/lib/security/cacerts -destkeystore /opt/apache-servicemix-6.0.0/keystore/livornoEsbKeystore.jks -srcstorepass changeit -deststorepass skpass -srcalias "srvesb.comuneli.local"
	public void transferCertificateByAlias(){
		
	}
}


