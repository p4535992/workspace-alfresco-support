#!/bin/bash

ALFRESCO_PATH="/opt/alfresco-community"
MAVEN_PATH="/opt/apache-maven-3.3.9"
DOWNLOAD_DIR="/usr/local/src"

ALFRESCO_AMP_PATH="$ALFRESCO_PATH/amps"
SHARE_AMP_PATH="$ALFRESCO_PATH/amps_share"

WEBDESKTOP_GROUPID="it.abd.webdesktop"
MAVEN_EXEC="$MAVEN_PATH/bin/mvn -U com.googlecode.maven-download-plugin:download-maven-plugin:1.2.1:artifact"
MAVEN_EXEC_W="$MAVEN_EXEC -DgroupId=$WEBDESKTOP_GROUPID"

WEBDESKTOP_WAR_ARTIFACT="webdesktop-test"
WEBDESKTOP_WAR_VERSION="1.0.0-SNAPSHOT"
WEBDESKTOP_CONTEXT_NAME="webdesktop"

OVERRIDE_ARTIFACT="webdesktop-override"
OVERRIDE_VERSION="1.0.15-SNAPSHOT"
OVERRIDE_CLASSIFIER="test-interno"

rm -f "$ALFRESCO_AMP_PATH"/*
rm -f "$SHARE_AMP_PATH"/*

echo "$(date "+%x %r") Start Deploy"
echo "Downloading Alfresco AMPS"
#eval "$MAVEN_EXEC -Dtype=amp -DoutputDirectory=$ALFRESCO_AMP_PATH -DgroupId=org.alfresco.integrations -DartifactId=alfresco-googledocs-repo -Dversion=3.0.3" #Alfresco standard
#eval "$MAVEN_EXEC -Dtype=amp -DoutputDirectory=$ALFRESCO_AMP_PATH -DgroupId=org.alfresco.aos-module -DartifactId=alfresco-aos-module -Dversion=1.1" #Alfresco standard
#eval "$MAVEN_EXEC -Dtype=amp -DoutputDirectory=$ALFRESCO_AMP_PATH -DgroupId=org.alfresco -DartifactId=alfresco-share-services -Dversion=5.1.e" #Alfresco standard

eval "$MAVEN_EXEC_W -Dtype=amp -DoutputDirectory=$ALFRESCO_AMP_PATH -DartifactId=webdesktop-amp -Dversion=1.2.1-SNAPSHOT"
eval "$MAVEN_EXEC_W -Dtype=amp -DoutputDirectory=$ALFRESCO_AMP_PATH -DartifactId=webdesktop-filing-classificazione-amp -Dversion=1.0.3 -Dclassifier=test-interno"
eval "$MAVEN_EXEC_W -Dtype=amp -DoutputDirectory=$ALFRESCO_AMP_PATH -DartifactId=webdesktop-protocollo-amp -Dversion=1.0.13-SNAPSHOT"
eval "$MAVEN_EXEC_W -Dtype=amp -DoutputDirectory=$ALFRESCO_AMP_PATH -DartifactId=webdesktop-conservazione-amp -Dversion=1.0.6-SNAPSHOT -Dclassifier=test-interno"
eval "$MAVEN_EXEC_W -Dtype=amp -DoutputDirectory=$ALFRESCO_AMP_PATH -DartifactId=webdesktop-albopretorio-amp -Dversion=1.0.1-SNAPSHOT"

echo "Downloading Share AMPS"
#eval "$MAVEN_EXEC -Dtype=amp -DoutputDirectory=$SHARE_AMP_PATH -DgroupId=org.alfresco.integrations -DartifactId=alfresco-googledocs-share -Dversion=3.0.3" #Alfresco standard

eval "$MAVEN_EXEC_W -Dtype=amp -DoutputDirectory=$SHARE_AMP_PATH -DartifactId=webdesktop-share-amp -Dversion=1.2.0-SNAPSHOT"
eval "$MAVEN_EXEC_W -Dtype=amp -DoutputDirectory=$SHARE_AMP_PATH -DartifactId=webdesktop-filing-classificazione-share-amp -Dversion=1.0.3"
eval "$MAVEN_EXEC_W -Dtype=amp -DoutputDirectory=$SHARE_AMP_PATH -DartifactId=webdesktop-conservazione-share-amp -Dversion=1.0.2-SNAPSHOT"
eval "$MAVEN_EXEC_W -Dtype=amp -DoutputDirectory=$SHARE_AMP_PATH -DartifactId=webdesktop-albopretorio-share-amp -Dversion=1.0.0-SNAPSHOT"

echo "Downloading Webdesktop WAR"
eval "$MAVEN_EXEC_W -Dtype=war -DoutputDirectory=$DOWNLOAD_DIR -DartifactId=$WEBDESKTOP_WAR_ARTIFACT -Dversion=$WEBDESKTOP_WAR_VERSION"

echo "Downloading Override"
eval "$MAVEN_EXEC_W -Dtype=jar -DoutputDirectory=$DOWNLOAD_DIR -DartifactId=$OVERRIDE_ARTIFACT -Dversion=$OVERRIDE_VERSION -Dclassifier=$OVERRIDE_CLASSIFIER"

echo "Download Done."

echo "Stopping Alfresco..."
service alfresco stop
echo "Alfresco Stopped, installing.."

#Eliminazione della libreria xmlsec
zip -d "$ALFRESCO_PATH"/tomcat/webapps/alfresco.war "WEB-INF/lib/xmlsec-1.4.5.jar"

eval "echo '' | $ALFRESCO_PATH/bin/apply_amps.sh -force"

echo "Installing WAR.."

rm -f "$ALFRESCO_PATH"/tomcat/webapps/"$WEBDESKTOP_CONTEXT_NAME".war
rm -Rf "$ALFRESCO_PATH"/tomcat/webapps/"$WEBDESKTOP_CONTEXT_NAME"
mv "$DOWNLOAD_DIR"/"$WEBDESKTOP_WAR_ARTIFACT"-"$WEBDESKTOP_WAR_VERSION".war "$ALFRESCO_PATH"/tomcat/webapps/"$WEBDESKTOP_CONTEXT_NAME".war

echo "Installing Override"

rm -f "$ALFRESCO_PATH"/tomcat/shared/lib/"$OVERRIDE_ARTIFACT"-"$OVERRIDE_VERSION"-"$OVERRIDE_CLASSIFIER".jar
mv "$DOWNLOAD_DIR"/"$OVERRIDE_ARTIFACT"-"$OVERRIDE_VERSION"-"$OVERRIDE_CLASSIFIER".jar "$ALFRESCO_PATH"/tomcat/shared/lib/"$OVERRIDE_ARTIFACT"-"$OVERRIDE_VERSION"-"$OVERRIDE_CLASSIFIER".jar

echo "Starting Alfresco.."
service alfresco start

echo "$(date "+%x %r") END!"