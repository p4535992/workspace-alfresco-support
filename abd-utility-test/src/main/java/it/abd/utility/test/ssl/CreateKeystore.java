package it.abd.utility.test.ssl;
/**
 * keytool -genkey -alias client -keyalg RSA -keystore testInternoTestKeystore.jks -dname "CN=Giorgio Rossi, OU=ABD, O=Sun, L=Arezzo, S=Italia, C=IT" -storepass password -keypass password
 * keytool -genkey -alias client -keyalg RSA -keystore keensoft_sign_code_valid-until_20170811.pfx -dname "CN=Giorgio Rossi, OU=ABD, O=Sun, L=Arezzo, S=Italia, C=IT" -storepass password -keypass password 
 * keytool -genkeypair -keystore clientkeystore.ks -keyalg RSA -alias "client" -keypass "password" -dname "cn=Client, c=US"
 * keytool -keystore clientkeystore.ks -genkey -alias client
 * 
 * OFFICIAL GENERATE KEYSTORE PER CONNETTORI
 * keytool -keystore ConnettorePACompliance.jks" -genkey -alias selfsign -keyalg RSA -keysize 2048 -validity 3650
 */
public class CreateKeystore {

	public static void main(String[] args) {
		// TODO DA REALIZZARE

	}
	
	//TODO DA REALIZZARE
	//keytool -keystore ConnettorePACompliance.jks" -genkey -alias selfsign -keyalg RSA -keysize 2048 -validity 3650
	public static void createKeystorePACompliance(){
		
	}
	
	//TODO DA REALIZZARE
	//keytool -genkeypair -keystore clientkeystore.ks -keyalg RSA -alias "client" -keypass "password" -validity 3650 -dname "cn=Client, c=US"
	public static void createKeystore(){
		
	}
	
	//TODO DA REALIZZARE
	//jarsigner -keystore keensoft_sign_code_valid-until_20170811.jks miniapplet-full_1_5.jar client
	public static void signKeystore(String pathToKeystore,String pathToJarToSign,String aliasOfCertificate){
		
	}

}
