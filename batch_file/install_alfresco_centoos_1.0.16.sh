#!/bin/bash

#-------
# Script for install Alfresco 5.2.f on CentOS
# is valid even for ubuntu server you just must replace
# the 'yum install XXX' in  'sudo apt-get -qq -y XXX install' 
#
# Inspired from:+
# https://github.com/loftuxab/alfresco-ubuntu-install/blob/master/alfinstall.sh
# 
# -------
echo "--- START ALFRESCO SCRIPT INSTALLER ---"

if [ "$(id -u)" != "0" ]; then
   echo "This script must be run as root" 1>&2
   exit 1
	#sudo su
fi

############################
#script function
############################

#
#https://gist.github.com/kongchen/6748525
#Usage: setProperty $key $value $filename
#setProperty(){
#  awk -v pat="^$1=" -v value="$1=$2" '{ if ($0 ~ pat) print value; else print $0; }' $3 > $3.tmp
#  mv $3.tmp $3
#}

#Usage:
#setProperty key value file
#or
#export setPropertyFile="/tmp/testfile.config"
#setProperty key1 value1
#setProperty key2 value2
#setProperty key2 value2
setProperty(){
	if [ -z "$1" ]; then
	  echo "No parameters provided, exiting..."
	  exit 1
	fi
	if [ -z "$2" ]; then
	  echo "Key provided, but no value, breaking"
	  exit 1
	fi
	if [ -z "$3" ] && [ -z "$setPropertyFile" ]; then
	  echo "No file provided or setPropertyFile is not set, exiting..."
	  exit 1
	fi

	if [ "$setPropertyFile" ] && [ "$3" ]; then
		echo "setPropertyFile variable is set AND filename in comamnd! Use only or the other. Exiting..."
		exit 1
	else
	  if [ "$3" ] && [ ! -f "$3" ]; then
		echo "File in command NOT FOUND!"
		exit 1
	  elif [ "$setPropertyFile" ] && [ ! -f "$setPropertyFile" ]; then
		echo "File in setPropertyFile variable NOT FOUND!"
		exit 1
	  fi
	fi

	if [ "$setPropertyFile" ]; then
	  file=$setPropertyFile
	else
	  file=$3
	fi

	awk -v pat="^$1=" -v value="$1=$2" '{ if ($0 ~ pat) print value; else print $0; }' "$file" > "$file".tmp
	mv "$file".tmp "$file"
}
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
#export ftps_proxy=http://192.168.1.188:3128/

echo
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echogreen "Alfresco CentoOS installer by 4535992."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo

echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echoblue "Your Properties"
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
#
export IP_ADDRESS=192.168.0.40
export HOSTNAME=server.alfresco.lan

export ALF_HOME=/opt/alfresco-community
export ALF_DATA_HOME=$ALF_HOME/alf_data
export CATALINA_HOME=$ALF_HOME/tomcat
export ALF_USER=alfresco
export ALF_GROUP=$ALF_USER
export APTVERBOSITY="-qq -y"
export TMP_INSTALL=/usr/local/src
export DEFAULTYESNO="y"
#Da sistemare la ricerca della versione di alfresco
export ALFRESCO_INSTALLER_URL=https://sourceforge.net/projects/alfresco/files/Alfresco%20201707%20Community/alfresco-community-installer-201707-linux-x64.bin/download
#Da sistemare le ricerca della versione java 
#export JDK_ORACLE_URL_RPM=http://download.oracle.com/otn-pub/java/jdk/8u131/jdk-8u131-linux-x64.rpm
#6,7,8
export JDK_VERSION=8
export JDK_RPM_PACKAGE=jdk-8u172-linux-x64.rpm
export MAVEN_VERSION=3.5.2

export ALF_ADMIN_PASS=admin
export ALF_NAME_SCRIPT_SERVICE=alfresco

export QT_INSTALLER=http://download.qt.io/archive/qt/4.8/4.8.4/qt-everywhere-opensource-src-4.8.4.tar.gz
export QT_TAR=qt-everywhere-opensource-src-4.8.4.tar.gz
export QT_FOLDER=qt-everywhere-opensource-src-4.8.4

export QT_CAD_INSTALLER=https://ribbonsoft.com/archives/qcad/qcad-3.21.2-trial-linux-x86_64.tar.gz
export QT_CAD_TAR=qcad-3.21.2-trial-linux-x86_64.tar.gz
export QT_CAD_FOLDER=qcad-3.21.2-trial-linux-x86_64

export TOMCAT_INSTALLER=http://it.apache.contactlab.it/tomcat/tomcat-8/v8.5.32/bin/apache-tomcat-8.5.32.zip
export TOMCAT_TAR=apache-tomcat-8.5.32.zip
export TOMCAT_FOLDER=apache-tomcat-8.5.32

echo "IP_ADDRESS:" $IP_ADDRESS
echo "HOSTNAME:" $HOSTNAME
echo "ALF HOME:" $ALF_HOME
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

echo "QT_INSTALLER:" $QT_INSTALLER
echo "QT_CAD_INSTALLER:" $QT_CAD_INSTALLER

#create the temp folder where put the installers
if [[ -d $TMP_INSTALL ]]; then
   #rm -rf alfrescoinstall 
   echo $TMP_INSTALL " directory already exists"
else
   mkdir $TMP_INSTALL 
   echo $TMP_INSTALL " created directory already exists"
fi

echo
echoblue "--- Preparing for install. Updating the package index files..."
#yum update && upgrade
echo

# read -e -p "Install screen${ques} [y/n] " -i "$DEFAULTYESNO" addScreen

####################################################################################
# LIBRARIES FOR  SUPPORT THE JAVA MACHINE ON CENTOOS
# SUPPORT GHRAPICAL PROGRAM ON CENTOS
# https://github.com/oktadeveloper/okta-aws-cli-assume-role/issues/58
# https://github.com/MachinePublishers/jBrowserDriver/issues/193
# https://github.com/yccheok/jstock/issues/83
# https://github.com/MachinePublishers/jBrowserDriver/issues/18
# https://stackoverflow.com/questions/22836219/centos-6-5-howto-install-gtk-version-2-8-0
# https://computingforgeeks.com/how-to-enable-and-use-ssh-x11-forwarding-on-vagrant-instances/
################################################################################################
yum install -y gtk2 gtk3 
yum install -y memesa-libGL sa-libGL-devel
yum install -y xorg-x11-xauth xorg-x11-apps.x86_64 xauth xterm
#library for dependency smartcard
yum install -y pcsc-lite-libs.x86_64

if ["`which screen`" = "" ]; then
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo "--- You need to install screen. screen is used for developing the application."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
yum -y install screen
fi

# read -e -p "Install mc${ques} [y/n] " -i "$DEFAULTYESNO" addMc

if [ "`which mc`" = "" ]; then
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo "--- You need to install mc. mc is used for developing the application."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
yum -y install mc
fi

# read -e -p "Install wget${ques} [y/n] " -i "$DEFAULTYESNO" addWget

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

# if [ "`which gzip`" = "" ]; then
# echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
# echo "--- You need to install gzip. gzip is used for read and extract archive."
# echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
# yum -y install gzip gunzip
# fi

# if [ "`which 7z`" = "" ]; then
# echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
# echo "--- You need to install 7zip. 7zip is used for read and extract archive."
# echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
# cat /etc/*release
# sudo yum install -y -q epel-release
# sudo rpm -U --quiet http://mirrors.kernel.org/fedora-epel/6/i386/epel-release-6-8.noarch.rpm
# sudo rpm --import /etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL-6
# sudo yum repolist
# sudo yum install -y -q p7zip p7zip-plugins
# fi

#Install some dependencies not bundled in the Alfresco installer:
echoblue "--- Install packages for Libre Office on alfresco ---"
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

#################################################################################
# Installation of Java Oracle
#search for if any older JDK versions are installed in your system
#rpm -qa | grep -E '^open[jre|jdk]|j[re|dk]'
#################################################################################
echoblue "--- Verify and Install of the JVM ---"
if [[ $(java -version 2>&1) == *"OpenJDK"* ]]; 
then 
	echored "--- WARNING: You have open jdk installed. Install Java JDK."; 
	ext=rpm
	jdk_version=$JDK_VERSION	
	read -e -p "Install Oracle Java 8${ques} [y/n] " -i "n" installjdk
	if [ "$installjdk" = "y" ]; then
	    #echoblue "Remove of OpenJDK..."
	    #yum -y remove java*
		#echoblue "Installing Oracle Java 8. Fetching packages..."
		#if [ -n "$1" ]; then
		#	if [ "$1" == "tar" ]; then
		#		ext="tar.gz"
		#	fi
		#fi
		readonly url="http://www.oracle.com"
		readonly jdk_download_url1="$url/technetwork/java/javase/downloads/index.html"
		readonly jdk_download_url2=$(curl -s $jdk_download_url1 | egrep -o "\/technetwork\/java/\javase\/downloads\/jdk${jdk_version}-downloads-.+?\.html" | head -1 | cut -d '"' -f 1)
		if [[ -z "$jdk_download_url2" ]]; then 
			echored "Could not get jdk download url - $jdk_download_url1"
		fi

		readonly jdk_download_url3="${url}${jdk_download_url2}"
		readonly jdk_download_url4=$(curl -s $jdk_download_url3 | egrep -o "http\:\/\/download.oracle\.com\/otn-pub\/java\/jdk\/[7-8]u[0-9]+\-(.*)+\/jdk-[7-8]u[0-9]+(.*)linux-x64.$ext")

		for dl_url in ${jdk_download_url4[@]}; do
			wget -O $TMP_INSTALL/$JDK_RPM_PACKAGE --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" -N $dl_url
		done
		rpm --nosignature -ivh --force --prefix=/usr/java/ $TMP_INSTALL/$JDK_RPM_PACKAGE
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
		wget -O $TMP_INSTALL/$JDK_RPM_PACKAGE --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" -N $dl_url
	done
	
	#delete /var/lib/alternatives/ on CentOS 6&7
	rpm --nosignature -ivh --force --prefix=/usr/java/ $TMP_INSTALL/$JDK_RPM_PACKAGE
	#rpm --nosignature -ivh /tmp/alfrescoinstall/jdk-8u141-linux-x64.rpm
	#rpm --nosignature -Uvh /tmp/alfrescoinstall/jdk-8u141-linux-x64.rpm
	#export JAVA_HOME=/usr/java/default
	#export JRE_HOME=/usr/java/jre
	#export PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin
		
	echo
	echogreen "--- Finished installing Oracle Java 8"
	echo
else 
	echoblue '--- You have already a jdk java'; 
fi
#################################################################################
# Install external tomcat version8
# https://geeksterminal.com/automatically-start-tomcat-service-on-boot/1112/
# https://www.digitalocean.com/community/tutorials/how-to-install-apache-tomcat-8-on-centos-7
# https://www.howtoforge.com/tutorial/how-to-install-tomcat-on-centos/
#################################################################################
echoblue "--- Install of Tomcat ---"
wget -O "/usr/local/src/"$TOMCAT_TAR $TOMCAT_INSTALLER
unzip "/usr/local/src/"$TOMCAT_TAR -d "/opt"
mv "/opt/"$TOMCAT_FOLDER /opt/apache-tomcat
chmod +x "/opt/apache-tomcat/bin/catalina.sh"
chmod +x "/opt/apache-tomcat/bin/startup.sh"
chmod +x "/opt/apache-tomcat/bin/shutdown.sh"
touch /etc/systemd/system/tomcat.service
cat >> /etc/systemd/system/tomcat.service << EOF
[Unit]
Description=Apache Tomcat Web Application Container
After=syslog.target network.target
[Service]
Type=forking
Environment=JAVA_HOME=$JAVA_HOME
Environment=CATALINA_PID=/opt/apache-tomcat/temp/tomcat.pid
Environment=CATALINA_HOME=/opt/apache-tomcat
Environment=CATALINA_BASE=/opt/apache-tomcat
Environment='CATALINA_OPTS=-Xms512M -Xmx1024M -server -XX:+UseParallelGC'
Environment='JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom'
ExecStart=/opt/apache-tomcat/bin/startup.sh
ExecStop=/opt/apache-tomcat/bin/shutdown.sh
Restart=on-failure
[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl start tomcat
systemctl enable tomcat
#groupadd tomcat
#useradd -s /bin/false -g tomcat -d "/opt/"$TOMCAT_FOLDER tomcat
#chown -hR tomcat:tomcat "/opt/"$TOMCAT_FOLDER

#cp "/opt/"$TOMCAT_FOLDER"/bin/catalina.sh" "/etc/init.d/tomcat"
#sudo ln -s /etc/init.d/tomcat /etc/rc2.d/S99tomcat
#chkconfig tomcat on
#systemctl enable tomcat

#################################################################################
# Installation of Maven 
#################################################################################
echoblue "--- Install of Maven ---"
#if [[ $(mvn -version 2>&1) == *"command not found"* ]]; then
if [ "`which mvn`" = "" ]; then
	echored "--- WARNING: You not have maven. Install Maven."; 
	#wget "http://mirrors.gigenet.com/apache/maven/maven-3/"$MAVEN_VERSION"/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" -P $TMP_INSTALL
	wget "https://archive.apache.org/dist/maven/maven-3/"$MAVEN_VERSION"/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" -P $TMP_INSTALL
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
echoblue "--- Install Alfresco Community ---"
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

echoblue "--- Set https connection ---"
#setProperty alfresco.port 443 $ALF_HOME"/tomcat/shared/classes/alfresco-global.properties"
#setProperty alfresco.protocol https $ALF_HOME"/tomcat/shared/classes/alfresco-global.properties"
#setProperty share.port 443 $ALF_HOME"/tomcat/shared/classes/alfresco-global.properties"
#setProperty share.protocol https $ALF_HOME"/tomcat/shared/classes/alfresco-global.properties"
setProperty cors.enabled true $ALF_HOME"/tomcat/shared/classes/alfresco-global.properties"
setProperty security.csrf.disabled false $ALF_HOME"/tomcat/shared/classes/alfresco-global.properties"

echoblue "--- Enabling CORS on Alfresco ECM ---"
mkdir $ALF_HOME"/modules/platform/"
wget -O $ALF_HOME"/modules/platform/enablecors-1.0.jar" https://artifacts.alfresco.com/nexus/service/local/repositories/releases/content/org/alfresco/enablecors/1.0/enablecors-1.0.jar

#echoblue "--- Install Alfresco Explorer "
#wget -O $ALF_HOME"/tomcat/webapps/api-explorer-1.4.war" https://artifacts.alfresco.com/nexus/service/local/repositories/releases/content/org/alfresco/api-explorer/1.4/api-explorer-1.4.war

###################################################################################
## https://www.rosehosting.com/blog/how-to-install-node-js-and-npm-on-centos-7/
###################################################################################

#Install Node.js and npm from the NodeSource repository
#We will install Node.js v6 LTS and npm from the NodeSource repository which depend on the EPEL repository being available.
#To enable the EPEL repository on your CentOS 7 VPS, issue the following command:
#yum -y install epel-release

#Once the EPEL repository is enabled run the following command to add the Node.js v6 LTS repository:
#curl --silent --location https://rpm.nodesource.com/setup_6.x | sudo bash -

#If you want to enable the Node.js v8 repository instead of the command above run the following command:
#curl --silent --location https://rpm.nodesource.com/setup_8.x | sudo bash -

#Once the NodeSource repository is enabled we can proceed with the Node.js v6 LTS and npm installation:
#yum -y install nodejs

#Install build tools
#To compile and install native addons from the npm repository we also need to install build tools:
#yum -y install gcc-c++ make

# In case you need to add other firewall rules to open ports in order 
# to access custom Alfresco services issue the ss command to get a 
# list of all the services running on your machine.
#ss -tulpn

# EXCEPTION WITH WEBDAV OF ALFRESCO
# https://www.cyberciti.biz/faq/rhel-redhat-centos-7-change-hostname-command/
#echoblue "--- setup your system hostname and assure that local resolution points to your server IP Address  ---"
#hostnamectl set-hostname $HOSTNAME
#hostnamectl set-hostname $HOSTNAME --pretty
#hostnamectl set-hostname $HOSTNAME --static
#hostnamectl set-hostname $HOSTNAME --transient

# [OPTIONAL] After the installation process finishes and Alfresco services are started issue 
# the below commands in order to open the following firewall ports to allow 
# external hosts in your network to connect to the web application.
#echoblue "--- Set up firewall rules ---"
#systemctl enable firewalld
#systemctl start firewalld
#firewall-cmd --add-port=8080/tcp 
#firewall-cmd --add-port=8443/tcp 
#firewall-cmd --add-port=7070/tcp 
#firewall-cmd --reload

##############################################################################
# Install Fonts for JAVA FX
# https://www.itzgeek.com/how-tos/linux/centos-how-tos/how-to-install-microsoft-truetype-fonts-in-centos-6-rhel-6.html
##############################################################################
yum install -y curl cabextract xorg-x11-font-utils fontconfig
wget https://downloads.sourceforge.net/project/mscorefonts2/rpms/msttcore-fonts-installer-2.6-1.noarch.rpm
rpm --nosignature -ivh /usr/local/src/msttcore-fonts-installer-2.6-1.noarch.rpm


# [OPTIONAL] Install LibreCad
#wget -O "/usr/local/src/librecad-2.2.0-0.1.rc1.fc29.x86_64.rpm" https://kojipkgs.fedoraproject.org//packages/librecad/2.2.0/0.1.rc1.fc29/x86_64/librecad-2.2.0-0.1.rc1.fc29.x86_64.rpm
######################################################################################
# Install Qt (4.8.X) for using qcad
# https://gist.github.com/darongE/9f2f264a02c456afb6a94689a1d9f92c
# https://hiteshkumarsofat.wordpress.com/2014/09/03/install-qt-and-qcad-manual/
# https://www.programering.com/a/MjM3kjNwATA.html
##########################################################################################
echoblue "--- Install of QT ---"
yum install -y gcc gcc-c++ libX11-devel libXext-devel libXtst-devel
wget -O "/usr/local/src/"$QT_TAR $QT_INSTALLER 
tar xfvz "/usr/local/src/"$QT_TAR  -C /usr/local/bin/
cd "/usr/local/bin/"$QT_FOLDER
#./configure --prefix=/usr/local -fast -opensource -release -no-qt3support -fontconfig -dbus -sm -qt-libpng -qt-libjpeg -qt-libmng -qt-zlib -openssl -opengl desktop -xrender -webkit -confirm-license -ldbus-1
#./configure --prefix=/usr/local -fast -opensource -release  -embedded -no-qt3support -fontconfig -sm -qt-libpng -qt-libjpeg -qt-libmng -qt-zlib -openssl -opengl desktop -xrender -webkit -confirm-license -ldbus-1
#./configure --prefix=/usr/local -opensource -release -no-qt3support -fontconfig -sm -qt-libpng -qt-libjpeg -qt-libmng -qt-zlib -openssl -opengl desktop -xrender -webkit -confirm-license -ldbus-1
./configure -opensource -release -confirm-license
#gmake or make https://stackoverflow.com/questions/1194957/what-is-the-difference-between-gmake-and-make
gmake
gmake install
export PATH=~/opt/qt-everywhere-opensource-src-4.8.4/bin:$PATH
###########################################################################
# Install QCad
###############################################################################
echoblue "--- Install of QCAD ---"
yum install -y glibc-devel.x86_64 glibc-devel.i686 mesa-libGLU.x86_64 mesa-libGLU.i686 libXrender.x86_64 libXrender.i686 fontconfig.x86_64 fontconfig.i686 libSM.x86_64 libSM.i686
#wget -O "/usr/local/src/qcad-3.21.2-trial-linux-x86_64.run" https://ribbonsoft.com/archives/qcad/qcad-3.21.2-trial-linux-x86_64.run
#chmod a+x "/usr/local/src/qcad-3.21.2-trial-linux-x86_64.run"
#./usr/local/src/qcad-3.21.2-trial-linux-x86_64.run
wget -O "/usr/local/src/"$QT_CAD_TAR $QT_CAD_INSTALLER
tar xfvz "/usr/local/src/"$QT_CAD_TAR -C /usr/local/bin/ && mv "/usr/local/bin/"$QT_CAD_FOLDER /usr/local/bin/qcad
#https://reguchi.wordpress.com/2017/07/26/virtualbox-xkbcommon/
export QT_XKB_CONFIG_ROOT=/usr/share/X11/xkb
# to test the qcad application
#cd /opt/qcad-3.21.2-trial-linux-x86_64/
#./qcad


##############################################################################
# Install librsvg
# https://webadventures.at/2012/04/29/convert-svg-png/
##############################################################################
yum install -y librsvg2-devel.x86_64 librsvg2-tools.x86_64
ln -s /bin/rsvg-convert /usr/local/bin/rsvg-convert

##############################################################################
# Installing GhostScript 
##############################################################################
yum install -y ghostscript
wget -O /usr/local/src/ghostscript-9.23-linux-x86_64.tgz https://github.com/ArtifexSoftware/ghostpdl-downloads/releases/download/gs923/ghostscript-9.23-linux-x86_64.tgz
tar -zxvf /usr/local/src/ghostscript-9.23-linux-x86_64.tgz  -C /usr/local/src/
cp -rf  /usr/local/src/ghostscript-9.23-linux-x86_64/gs-923-linux-x86_64  /usr/local/bin/gs-923-linux-x86_64
#cd /usr/local/src/ghostscript-9.23-linux-x86_64/
#./configure
#make
#make install
##############################################################################
# Installing SWF TOOLS - pdf2swf 
##############################################################################
yum install -y zlib-devel libjpeg-devel giflib-devel freetype-devel gcc gcc-c++ 
wget -O /usr/local/src/swftools-0.9.2.tar.gz http://www.swftools.org/swftools-0.9.2.tar.gz
tar -zxvf /usr/local/src/swftools-0.9.2.tar.gz  -C /usr/local/src/
cd /usr/local/src/swftools-0.9.2/
./configure
make
make install

##############################################################################
# Installing Flash Player
# https://tecadmin.net/install-adobe-flash-player-on-centos-redhat-fedora/
##############################################################################
rpm -ivh http://linuxdownload.adobe.com/adobe-release/adobe-release-x86_64-1.0-1.noarch.rpm
rpm --import /etc/pki/rpm-gpg/RPM-GPG-KEY-adobe-linux
yum install flash-plugin nspluginwrapper alsa-plugins-pulseaudio libcurl

##############################################################################
# Install Apache batik
##############################################################################
#wget -O /usr/local/bin/batik-rasterizer-1.7.jar http://central.maven.org/maven2/org/apache/xmlgraphics/batik-rasterizer/1.7/batik-rasterizer-1.7.jar
wget -O /usr/local/src/batik-1.7.1.zip https://www.apache.org/dist/xmlgraphics/batik/binaries/batik-1.7.1.zip
unzip -a /usr/local/src/batik-1.7.1.zip -d /usr/local/bin/
##############################################################################
# Install Apache ServiceMix 6
# https://confluence.si.edu/display/SIDKB/Install+and+Configure+ServiceMix
##############################################################################
wget -O /usr/local/src/apache-servicemix-6.0.0.zip http://archive.apache.org/dist/servicemix/servicemix-6/6.0.0/apache-servicemix-6.0.0.zip
unzip -a /usr/local/src/apache-servicemix-6.0.0.zip -d /opt/
#cd /opt/apache-servicemix-6.0.0/bin/
#./karaf
#feature:install wrapper
#wrapper:install -s AUTO_START -n servicemix6 -d servicemix6 -D <"Service ServiceMix6">
#logout
#cp /opt/apache-servicemix-6.0.0/bin/servicemix6-service /opt/apache-servicemix-6.0.0/bin/servicemix6-service.orig
#cp /opt/apache-servicemix-6.0.0/bin/setenv /opt/apache-servicemix-6.0.0/bin/setenv.orig
#cp /opt/apache-servicemix-6.0.0/etc/servicemix6-wrapper.conf /opt/apache-servicemix-6.0.0/etc/servicemix6-wrapper.conf.orig

##############################################################################
# Install NTPD
##############################################################################


##############################################################################
# Install Percona Server
##############################################################################
#yum install -y http://www.percona.com/downloads/percona-release/redhat/0.1-4/percona-release-0.1-4.noarch.rpm
#yum install -y Percona-Server-server-57
#3x5zzt&w?ZPJ#rPh
##############################################################################
# [DA FINIRE]Install ImageMagick
##############################################################################
#wget -O "/usr/local/bin/ImageMagick-7.0.8-9.x86_64.rpm" https://www.imagemagick.org/download/linux/CentOS/x86_64/ImageMagick-7.0.8-9.x86_64.rpm
#wget -O "/usr/local/bin/ImageMagick-libs-7.0.8-9.x86_64.rpm" https://www.imagemagick.org/download/linux/CentOS/x86_64/ImageMagick-libs-7.0.8-9.x86_64.rpm
#rpm -Uvh "/usr/local/bin/ImageMagick-7.0.8-9.x86_64.rpm"
#rpm -Uvh "/usr/local/bin/ImageMagick-libs-7.0.8-9.x86_64.rpm"


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

