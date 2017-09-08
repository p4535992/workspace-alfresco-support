Create a self-signed certificate (=client-key-pair) in client-keystore.jks (for the client to sign his request)

$ keytool -genkey -alias client -keystore keystore.jks -storepass password  -keyalg RSA -validity 360 -keysize 2048

Extract the client-public-key.cer from client-keystore.jks

$ keytool -export -alias client -keystore client-keystore.jks  -storepass password -file client-public-key.cer

Import client's clientpublickey.cer into server's truststore.jks (for verifying the client's request signature)

$ keytool -import -alias client -keystore client-keystore.jks -storepass password -file client-public-key.cer

Now I can receive signed requests from the client, right? But what about signing the response?

Create a 2nd self-signed certificate (=server-key-pair) in server-keystore.jks (for the server to sign his response)

$ keytool -genkey -alias server -keystore server-keystore.jks -storepass password  -keyalg RSA -validity 360 -keysize 2048

Extract the server-public-key.cer from server-keystore.jks

$ keytool -export -alias server -keystore server-keystore.jks -storepass password -file server-public-key.cer

Import server-public-key.cer into client-truststore.jks (for verifying the server's response signature)

$ keytool -import -alias server -keystore client-truststore.jks -storepass password -file server-public-key.cer

In the end I have four (key) stores containing two different certificates:
For the client: client-keystore (client-key-pair) & client-truststore (server-public-key)
For the server: server-keystore (server-key-pair) & server-truststore (client-public-key)
