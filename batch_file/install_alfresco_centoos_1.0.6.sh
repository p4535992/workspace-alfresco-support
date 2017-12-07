#!/bin/bash

#-------
# Script for install of Alfresco
#
# 2017 ABD
#
# PROBLEMI DA RISOLVERE :
#  1)Se si installa più volte la JVM tende a manifestare problemi di unpackaging
#  2) Rendere più dinamica la scelta della versione dell JVM (per ora prende sempre la più recente della versione setatta)
#  3) Rendere più dinamica la scelta della versione di alfresco.
#
# -------
echo "--- START ALFRESCO SCRIPT INSTALLER ---"

if [ "$(id -u)" != "0" ]; then
   echo "This script must be run as root" 1>&2
   exit 1
	#sudo su
fi

#
# Color variables
txtund=$(tput sgr 0 1)          # Underline
txtbld=$(tput bold)             # Bold
bldred=${txtbld}$(tput setaf 1) #  red
bldgre=${txtbld}$(tput setaf 2) #  red
bldblu=${txtbld}$(tput setaf 4) #  blue
bldwht=${txtbld}$(tput setaf 7) #  white
txtrst=$(tput sgr0)             # Reset
info=${bldwht}*${txtrst}        # Feedback
pass=${bldblu}*${txtrst}
warn=${bldred}*${txtrst}
ques=${bldblu}?${txtrst}
#
echoblue(){
	echo "${bldblu}$1${txtrst}"
}
echored(){
	echo "${bldred}$1${txtrst}"
}
echogreen(){
	echo "${bldgre}$1${txtrst}"
}

#if you use a proxy

#export http_proxy=http://192.168.1.188:3128/
#export https_proxy=http://192.168.1.188:3128/
#export ftp_proxy=http://192.168.1.188:3128/

echo
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echogreen "Alfresco CentoOS installer by Marco Tenti."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo

echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echoblue "Your Properties"
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
#
export ALF_HOME=/opt/alfresco-community-201707
export ALF_DATA_HOME=$ALF_HOME/alf_data
export CATALINA_HOME=$ALF_HOME/tomcat
export ALF_USER=alfresco
export ALF_GROUP=$ALF_USER
export APTVERBOSITY="-qq -y"
export TMP_INSTALL=/tmp/alfrescoinstall
#Da sistemare la ricerca della versione di alfresco
export ALFRESCO_INSTALLER_URL=https://sourceforge.net/projects/alfresco/files/Alfresco%20201707%20Community/alfresco-community-installer-201707-linux-x64.bin/download
#Da sistemare le ricerca della versione java 
#export JDK_ORACLE_URL_RPM=http://download.oracle.com/otn-pub/java/jdk/8u131/jdk-8u131-linux-x64.rpm
#6,7,8
export JDK_VERSION=8
export MAVEN_VERSION=3.3.9

export ALF_ADMIN_PASS=admin
export ALF_NAME_SCRIPT_SERVICE=alfresco

echo "PATH INSTALL ALFRESCO:" $ALF_HOME
echo "ALF_DATA_HOME:" $ALF_DATA_HOME
echo "CATALINA_HOME:" $CATALINA_HOME
echo "ALF_USER:" $ALF_USER
echo "ALF_GROUP:" $ALF_USER
echo "TMP_INSTALL:" $TMP_INSTALL
#Da sistemare la ricerca della versione di alfresco
echo "ALFRESCO_INSTALLER_URL:" $ALFRESCO_INSTALLER_URL
echo "JDK_VERSION:" $JDK_VERSION "(you donwload the last version)"
echo "MAVEN_VERSION:" $MAVEN_VERSION
echo "ALF_ADMIN_PASS:" $ALF_ADMIN_PASS
echo "ALF_NAME_SCRIPT_SERVICE:" $ALF_NAME_SCRIPT_SERVICE

#create the temp folder where put the installers
if [[ -d $TMP_INSTALL ]]; then
   rm -rf alfrescoinstall 
else
   mkdir $TMP_INSTALL
fi

echo
echoblue "--- Preparing for install. Updating the package index files..."
#yum update && upgrade
echo

if [ "`which screen`" = "" ]; then
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo "--- You need to install screen. screen is used for developing the application."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
yum -y install screen
fi

if [ "`which mc`" = "" ]; then
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo "--- You need to install mc. mc is used for developing the application."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
yum -y install mc
fi

if [ "`which wget`" = "" ]; then
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo "--- You need to install wget. Wget is used for downloading components to install."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
yum -y install wget
fi

if [ "`which sed`" = "" ]; then
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo "--- You need to install sed. Sed is used for replace content of a file."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
yum -y install sed
fi

if [ "`which tar`" = "" ]; then
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo "--- You need to install tar. Tar is used for read and extract gz archive."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
yum -y install tar
fi

if [ "`which rpm`" = "" ]; then
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo "--- You need to install rpm. rpm is used for read and extract gz archive."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
yum -y install rpm
fi

echoblue "--- Installiamo gli spacchettizatori per poter leggere i pacchetti compressi con midnight commander ---"

if [ "`which zip`" = "" ]; then
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo "--- You need to install zip. zip is used for read and extract archive."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
yum -y install zip unzip
fi

if [ "`which gzip`" = "" ]; then
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo "--- You need to install gzip. gzip is used for read and extract archive."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
yum -y install gzip gunzip
fi

if [ "`which 7z`" = "" ]; then
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo "--- You need to install 7zip. 7zip is used for read and extract archive."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
cat /etc/*release
sudo yum install -y -q epel-release
sudo rpm -U --quiet http://mirrors.kernel.org/fedora-epel/6/i386/epel-release-6-8.noarch.rpm
sudo rpm --import /etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL-6
sudo yum repolist
sudo yum install -y -q p7zip p7zip-plugins
fi

#Install some dependencies not bundled in the Alfresco installer:
echoblue "--- Installiamo i pacchetti base per Libre Office di alfresco ---"
if [ "`which fc-query`" = "" ]; then
	#Some or all of the libraries needed to support LibreOffice were not found on your system: fontconfig libSM libICE libXrender libXext libcups libGLU libcairo2 libgl1-mesa-glx
	sudo yum -y install fontconfig libSM libICE libXrender libXext libcups libGLU libcairo2 libgl1-mesa-glx
	#sudo yum -y install llibXt libXrender libSM libICE libXext fontconfig cups-libs mesa-libGLU libGLU 
	#sudo yum install cairo-devel

fi
#echo
#echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
#echo "You need to add a system user that runs the tomcat Alfresco instance."
#echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
#read -e -p "Add alfresco system user${ques} [y/n] " -i "n" addalfresco
#if [ "$addalfresco" = "y" ]; then
#  sudo adduser --system --disabled-login --disabled-password --group $ALF_USER
#  echo
#  echogreen "Finished adding alfresco user"
#  echo
#else
#  echo "Skipping adding alfresco user"
#  echo
#fi

#Operazioni consigliate dagli esperti 
#Create the alfresco group
echoblue "--- Create the alfresco group"
if [ $(getent group alfresco) ]; then
  echored "--- groupadd: group 'alfresco' already exists"
else
  #echo "group does not exist."
  sudo groupadd alfresco 
fi
#Create the alfresco user:
#sudo adduser-m -g alfresco alfresco 
#Define a password for the alfresco user:
#Inserimento manuale nuovo utente alfresco
#sudo passwd alfresco
#Inserimento automatico
#echo alfresco:nuova_password | chpasswd 

#Add the root user to the alfresco group too:
echoblue "--- Add the root user to the alfresco group too"
sudo usermod -a -G alfresco root

#search for if any older JDK versions are installed in your system
#rpm -qa | grep -E '^open[jre|jdk]|j[re|dk]'
echoblue "--- Verify of the JVM ---"
if [[ $(java -version 2>&1) == *"OpenJDK"* ]]; 
then 
	echored "--- WARNING: You have open jdk installed. Install Java JDK."; 
	ext=rpm
	jdk_version=$JDK_VERSION	
	read -e -p "Install Oracle Java 8${ques} [y/n] " -i "n" installjdk
	if [ "$installjdk" = "y" ]; then
	    echoblue "Remove of OpenJDK..."
	    yum -y remove java*
		echoblue "Installing Oracle Java 8. Fetching packages..."
		if [ -n "$1" ]; then
			if [ "$1" == "tar" ]; then
				ext="tar.gz"
			fi
		fi
		readonly url="http://www.oracle.com"
		readonly jdk_download_url1="$url/technetwork/java/javase/downloads/index.html"
		readonly jdk_download_url2=$(curl -s $jdk_download_url1 | egrep -o "\/technetwork\/java/\javase\/downloads\/jdk${jdk_version}-downloads-.+?\.html" | head -1 | cut -d '"' -f 1)
		if [[ -z "$jdk_download_url2" ]]; then 
			echored "Could not get jdk download url - $jdk_download_url1"
		fi

		readonly jdk_download_url3="${url}${jdk_download_url2}"
		readonly jdk_download_url4=$(curl -s $jdk_download_url3 | egrep -o "http\:\/\/download.oracle\.com\/otn-pub\/java\/jdk\/[7-8]u[0-9]+\-(.*)+\/jdk-[7-8]u[0-9]+(.*)linux-x64.$ext")

		for dl_url in ${jdk_download_url4[@]}; do
			wget -O $TMP_INSTALL/jdk-8u141-linux-x64.rpm --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" -N $dl_url
		done
		rpm --nosignature -ivh --force --prefix=/usr/java/ $TMP_INSTALL/jdk-8u141-linux-x64.rpm
		#rpm --nosignature -ivh /tmp/alfrescoinstall/jdk-8u141-linux-x64.rpm
		export JAVA_HOME=/usr/java/default
		export JRE_HOME=/usr/java/jre
		export PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin
		
		echo
		echogreen "--- Finished installing Oracle Java 8"
		echo
	else
	  echo "Skipping install of Oracle Java 8"
	  echored "--- IMPORTANT: You need to install other JDK and adjust paths for the install to be complete"
	  echo
	fi
elif [[ $(java -version 2>&1) == *"command not found"* ]];
then
	echored "--- WARNING: You have no java machine. Install Java JDK."; 
	ext=rpm
	jdk_version=$JDK_VERSION	
	if [ -n "$1" ]; then
		if [ "$1" == "tar" ]; then
			ext="tar.gz"
		fi
	fi
	readonly url="http://www.oracle.com"
	readonly jdk_download_url1="$url/technetwork/java/javase/downloads/index.html"
	readonly jdk_download_url2=$(curl -s $jdk_download_url1 | egrep -o "\/technetwork\/java/\javase\/downloads\/jdk${jdk_version}-downloads-.+?\.html" | head -1 | cut -d '"' -f 1)
	
	if [[ -z "$jdk_download_url2" ]]; then 
		echored "Could not get jdk download url - $jdk_download_url1 with $jdk_download_url2"
	fi

	readonly jdk_download_url3="${url}${jdk_download_url2}"
	readonly jdk_download_url4=$(curl -s $jdk_download_url3 | egrep -o "http\:\/\/download.oracle\.com\/otn-pub\/java\/jdk\/[7-8]u[0-9]+\-(.*)+\/jdk-[7-8]u[0-9]+(.*)linux-x64.$ext")

	for dl_url in ${jdk_download_url4[@]}; do
		wget -O $TMP_INSTALL/jdk-8u141-linux-x64.rpm --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" -N $dl_url
	done
	
	#delete /var/lib/alternatives/ on CentOS 6&7
	rpm --nosignature -ivh --force --prefix=/usr/java/ $TMP_INSTALL/jdk-8u141-linux-x64.rpm
	#rpm --nosignature -ivh /tmp/alfrescoinstall/jdk-8u141-linux-x64.rpm
	#rpm --nosignature -Uvh /tmp/alfrescoinstall/jdk-8u141-linux-x64.rpm
	export JAVA_HOME=/usr/java/default
	export JRE_HOME=/usr/java/jre
	export PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin
		
	echo
	echogreen "--- Finished installing Oracle Java 8"
	echo
else 
	echoblue '--- You have already a jdk java'; 
fi

echoblue "--- Verify of Maven ---"
#if [[ $(mvn -version 2>&1) == *"command not found"* ]]; then
if [ "`which mvn`" = "" ]; then
	echored "--- WARNING: You not have maven. Install Maven."; 
	wget "http://mirrors.gigenet.com/apache/maven/maven-3/"$MAVEN_VERSION"/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" -P $TMP_INSTALL
	tar -zxvf $TMP_INSTALL"/apache-maven-"$MAVEN_VERSION"-bin.tar.gz" -C /opt/
	#Settiamo la variabile di ambiente MAVEN_HOME sulla macchina:
	export M2_HOME='/opt/apache-maven-'$MAVEN_VERSION
	export M2="$M2_HOME/bin"
	export PATH="$PATH:$M2"
	echogreen "--- Finished installing Maven"
else
	echoblue '--- You have already Maven'; 
fi

#Create the folder where we'll install Alfresco and give access to our newly created user:
echoblue "--- Create the folder where we'll install Alfresco and give access to our newly created user ---"
if [[ -d $TMP_INSTALL ]]; then
   echo 
else
   sudo mkdir $ALF_HOME
fi
#sudo chown alfresco:root $ALF_HOME

#echo "--- Scriviamo il servizio di supporto su /etc/init.d (consigliato dagli esperti come aggiuna a quello creato da automatico dall'installer ---"
#cat > /etc/init.d/alfresco-service <<- EOF
#!/bin/sh -e
#
#ALFRESCO_SCRIPT="/opt/alfresco/alfresco.sh"
#
#if [ "$1" = "start" ]; then
# su - alfresco "${ALFRESCO_SCRIPT}" "start"
#elif [ "$1" = "stop" ]; then
# su - alfresco "${ALFRESCO_SCRIPT}" "stop"
#elif [ "$1" = "restart" ]; then
# su - alfresco "${ALFRESCO_SCRIPT}" "stop"
# su - alfresco "${ALFRESCO_SCRIPT}" "start"
#else
# echo "Usage: /etc/init.d/alfresco [start|stop|restart]"
#fi
#EOF

#cat > /etc/init.d/alfresco-service <<- EOF 
#!/bin/sh 
# 
#        RETVAL=0 
# 
#        start () { 
#            /opt/alfresco-community/alfresco.sh start "$2" 
#            RETVAL=$? 
#            if [ -d "/var/lock/subsys" ]&& [ `id -u` = 0 ] && 
#             [ $RETVAL -eq 0 ] ; then 
#                  touch /var/lock/subsys/alfresco 
#            fi 
# 
#        } 
# 
#        stop () { 
#            /opt/alfresco-community/alfresco.sh stop "$2" 
#            RETVAL=$? 
#        } 
#  
#        case "$1" in 
#            start) 
#                start "$@" 
#                ;; 
#            stop) 
#                stop "$@" 
#                ;; 
#            restart) 
#                stop "$@" 
#                start "$@" 
#                ;; 
#            *) 
#                /opt/alfresco-community/alfresco.sh "$@" 
#                RETVAL=$? 
#        esac 
#        exit $RETVAL 
#EOF
#sed -i -e 's+$PATH_ALFRESCO+XXXXXXXXXX+g' /etc/init.d/alfresco-service
#Make this script executable
#sudo chmod +x /etc/init.d/alfresco-service        

#Make the installer executable:
echoblue "--- Install Alfresco Community "
if service --status-all | grep -Fq 'alfresco'; then    
	echored "--- You already have a alfresco update the script to manage this case or set manually a new alfresco server"	
else
	wget -O $TMP_INSTALL/myAlfrescoInstaller.bin $ALFRESCO_INSTALLER_URL
	chmod +x $TMP_INSTALL/myAlfrescoInstaller.bin
	echo "--- You are installing alfresco community on the path:" $ALF_HOME "wait for 5 minutes...."
	#sudo /tmp/alfrescoinstall/myAlfrescoInstaller.bin --installer-language it --prefix /opt/alfresco-community-201707 --alfresco_admin_password admin
	sudo $TMP_INSTALL/myAlfrescoInstaller.bin --mode unattended --installer-language it --prefix $ALF_HOME --alfresco_admin_password $ALF_ADMIN_PASS --baseunixservice_script_name $ALF_NAME_SCRIPT_SERVICE --enable-components javaalfresco,postgres,libreofficecomponent,alfrescosolr4,aosmodule,alfrescowcmqs,alfrescogoogledocs --disable-components alfrescosolr
	echo "--- ... You have installed alfresco community on the path:" $ALF_HOME
	echogreen "--- Finished installing Alfresco Community"
fi

echogreen "--- END ALFRESCO SCRIPT INSTALLER ---"




#SETTAGGIO MIDNIGHT
#Midnight commander tries to open war files as tgz files. Is should open them as zip files instead.
#To fix this open "Edit extension file" in "Command" menue (F9, C, e). Search for ".war" and delete it from the listings. The modified listing should look like:

# .tgz, .tpz, .tar.gz, .tar.z, .tar.Z, .ipk, .gem
#regex/\.t([gp]?z|ar\.g?[zZ])$|\.ipk$|\.gem$
#<------>Open=%cd %p/utar://
#<------>View=%view{ascii} /usr/libexec/mc/ext.d/archive.sh view tar.gz

#Then add the following snippet somewhere in the file:

# .war, .amp
#regex/\.war$|.\amp$
#<------>Open=%cd %p/uzip://
#<------>View=%view{ascii} /usr/libexec/mc/ext.d/archive.sh view zip

