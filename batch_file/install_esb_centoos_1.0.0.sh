#!/bin/bash

#-------
# Script for install of Alfresco
#
# 2017 ABD
#
# PROBLEMI DA RISOLVERE :
#  1)Le specifiche di questo script sono relative a una macchina standard di 4G
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
export SERVICEMIX_HOME=/opt/apache-servicemix-6.0.0
export SERVICEMIX_DATA_HOME=$SERVICEMIX_HOME/data
export TMP_INSTALL=/tmp/servicemixinstall
#Da sistemare la ricerca della versione di alfresco
export SERVICEMIX_VERSION=6.0.0
export SERVICEMIX_INSTALLER_URL=http://archive.apache.org/dist/servicemix/servicemix-6/6.0.0/apache-servicemix-6.0.0.zip
#Da sistemare le ricerca della versione java 
#export JDK_ORACLE_URL_RPM=http://download.oracle.com/otn-pub/java/jdk/8u131/jdk-8u131-linux-x64.rpm
#6,7,8
export JDK_VERSION=8
export MAVEN_VERSION=3.3.9

export SERVICEMIX_WEBCONSOLE_USER=smx
export SERVICEMIX_WEBCONSOLE_PASS=smx
export SERVICEMIX_PASS_ADMINABDGROUP=kdflgft82
export SERVICEMIX_NAME_SCRIPT_SERVICE=servicemix

echo "SERVICEMIX_HOME:" $SERVICEMIX_HOME
echo "SERVICEMIX_DATA_HOME:" $SERVICEMIX_DATA_HOME
echo "TMP_INSTALL:" $TMP_INSTALL
#Da sistemare la ricerca della versione di servicemix
echo "SERVICEMIX_VERSION:" $SERVICEMIX_VERSION
echo "SERVICEMIX_INSTALLER_URL:" $SERVICEMIX_INSTALLER_URL
echo "JDK_VERSION:" $JDK_VERSION "(you donwload the last version)"
echo "MAVEN_VERSION:" $MAVEN_VERSION
echo "SERVICEMIX_WEBCONSOLE_USER:" $SERVICEMIX_WEBCONSOLE_USER
echo "SERVICEMIX_WEBCONSOLE_PASS:" $SERVICEMIX_WEBCONSOLE_PASS
echo "SERVICEMIX_PASS_ADMINABDGROUP:" $SERVICEMIX_PASS_ADMINABDGROUP
echo "SERVICEMIX_NAME_SCRIPT_SERVICE" $SERVICEMIX_NAME_SCRIPT_SERVICE

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

if [ "`which sed`" = "" ]; then
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo "--- You need to install sed. sed is used for modify content of the file."
echoblue "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
yum -y install sed
fi

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
	export M2_HOME='/opt/apache-maven-'$MAVEN_VERSION''
	export M2="$M2_HOME/bin"
	export PATH="$PATH:$M2"
	echogreen "--- Finished installing Maven"
else
	echoblue '--- You have already Maven'; 
fi

echoblue "--- Install of ServiceMix ---"

if [ ! -d "$SERVICEMIX_HOME" ]; then
	# Control will enter here if $DIRECTORY doesn't exist.
	wget $SERVICEMIX_INSTALLER_URL -P $TMP_INSTALL
	unzip -a apache-servicemix-$SERVICEMIX_VERSION.zip -d /opt/
	
	#Appendiamo il testo su setenv
	#To append a contains of bar.txt to to foo.txt, enter
	#cat $SERVICEMIX_HOME/bin/setenv >> setenv2	
	echo '#MOD ABD INIZIO\nexport JAVA_HOME=/usr/java/default/\nexport M2_HOME="/opt/apache-maven-'"$MAVEN_VERSION"'\nexport M2="$M2_HOME/bin\nexport PATH="$PATH:$M2\nexport PATH=”$PATH:$JAVA_HOME/bin”\nexport JAVA_MIN_MEM=2048M\nexport JAVA_MAX_MEM=3072M\n#MOD ABD FINE' >> $SERVICEMIX_HOME/bin/setenv
	
	#Appendiamo il testo su user.properties
	echo 'abd = '"$SERVICEMIX_PASS_ADMINABDGROUP"',_g_:admingroup' >> $SERVICEMIX_HOME/etc/users.properties
	
	#chmod +x $SERVICEMIX_HOME/bin/servicemix
	#sh $SERVICEMIX_HOME/bin/servicemix
	$SERVICEMIX_HOME/bin/servicemix
	
	# start in background
	#./bin/start
	# wait till SSH daemon is up
	#sleep 5
	# run your commands
	#./bin/client -u $SERVICEMIX_WEBCONSOLE_USER -p $SERVICEMIX_WEBCONSOLE_PASS "your command here" 
	
	# wait till SSH daemon is up
	sleep 10
	
	feature:install webconsole
	#installazione automatica con il nome del servizio KARAF-service
	#feature:install wrapper
	
	#wrapper:install -s manual -n servicemix -d servicemix
	#wrapper:install -s AUTO_START -n KARAF -d Karaf -D Karaf
	#wrapper:install -s AUTO_START -n servicemix6 -d servicemix6 -D $SERVICEMIX_NAME_SCRIPT_SERVICE
	#wrapper:install -s manual -n servicemix6 -d servicemix6 -D "ServiceMix 6"
	#wrapper:install -s AUTO_START -n servicemix6 -d servicemix6 -D "ServiceMix 6"
	#wrapper:install -s AUTO_START -n KARAF -d Karaf -D "Karaf Service"
	
	wrapper:install -s AUTO_START -n $SERVICEMIX_NAME_SCRIPT_SERVICE -d $SERVICEMIX_NAME_SCRIPT_SERVICE -D $SERVICEMIX_NAME_SCRIPT_SERVICE
	shutdown
	yes
	
	#replace the content on the wrapper-conf file service
	sed -i 's/wrapper.java.command=java/wrapper.java.command=%JAVA_HOME%/bin/java\nwrapper.java.maxmemory=3072/g;' $SERVICEMIX_HOME/etc/$SERVICEMIX_NAME_SCRIPT_SERVICE-wrapper-conf
	#remove symboli link and copy the script to etc/init.d
	rm /etc/init.d/$SERVICEMIX_NAME_SCRIPT_SERVICE-service
	cp $SERVICEMIX_HOME/etc/$SERVICEMIX_NAME_SCRIPT_SERVICE-wrapper-conf /etc/init.d/
	mv /etc/init.d/$SERVICEMIX_NAME_SCRIPT_SERVICE-wrapper-conf /etc/init.d/$SERVICEMIX_NAME_SCRIPT_SERVICE-service
		
	echogreen "--- Finished installing servicemix"
else
	echoblue '--- You have already servicemix'; 
fi

echoblue "--- Install of mysql ---"
if [ "`which mysql`" = "" ]; then
	# Enable Ubuntu Firewall and allow SSH & MySQL Ports
	ufw enable
	ufw allow 22
	ufw allow 3306

	# Install essential packages
	yum -y install zsh htop
	# Install MySQL Server in a Non-Interactive mode. Default root password will be "root"
	echo "mysql-server-5.6 mysql-server/root_password password root" | sudo debconf-set-selections
	echo "mysql-server-5.6 mysql-server/root_password_again password root" | sudo debconf-set-selection
	#informarsi se per caso occorre mettere “mariadb” al posto di mysql
	yum -y install mysql-server-5.6
	
	# Run the MySQL Secure Installation wizard
	#/usr/bin/mysql_secure_installation
	echo -e "root\nn\nY\nY\nY\nY\n" | mysql_secure_installation

	sed -i 's/127\.0\.0\.1/0\.0\.0\.0/g' /etc/mysql/my.cnf
	mysql -uroot -p -e 'USE mysql; UPDATE `user` SET `Host`="%" WHERE `User`="root" AND `Host`="localhost"; DELETE FROM `user` WHERE `Host` != "%" AND `User`="root"; FLUSH PRIVILEGES;'
	
	service mysql restart
		
	chkconfig mysqld on
	echogreen "--- Finished installing mysql"
else
	echoblue '--- You have already mysql'; 
fi

#create setting.xml di maven per importare le librerie abd
cat <<EOF > /root/.m2/settings.xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
		  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <pluginGroups></pluginGroups>
  <proxies>
  </proxies>
  <servers>
	<server>
	  <id>abd-releases</id>
	  <username>externalApp</username>
	  <password>het5w6xlo91</password>
	</server>
	<server>      
		<id>abd-snapshots</id>      
		<username>externalApp</username>      
		<password>het5w6xlo91</password>    
	</server>  
	<server>
	  <id>centralMirror</id>
	  <username>externalApp</username>
	  <password>het5w6xlo91</password>
	</server>
  </servers>
  <mirrors>
	<mirror>
	  <id>centralMirror</id>
	  <mirrorOf>central</mirrorOf>
	  <name>Human Readable Name for this Mirror.</name>
	  <url>http://localhost:8687/nexus/content/repositories/central/</url>
	</mirror>     
  </mirrors>
	<profiles> 
		<profile> 
			<id>abd</id> 
			<activation> 
				<activeByDefault>true</activeByDefault> 
			</activation>
			<repositories>
				<repository> 
					<id>abd-releases</id> 
					<name>releases</name> 
					<url>http://localhost:8687/nexus/content/repositories/releases/</url> 
					<snapshots> 
						<enabled>false</enabled> 
					</snapshots>
				</repository>
				<repository> 
					<id>abd-snapshots</id> 
					<name>snapshots</name> 
					<url>http://localhost:8687/nexus/content/repositories/snapshots/</url> 
					<snapshots> 
					<enabled>true</enabled> 
					</snapshots>
				</repository>
			</repositories>
		</profile> 
	</profiles> 
	<activeProfiles> 
		<activeProfile>abd</activeProfile> 
	</activeProfiles> 
</settings>
EOF

echogreen "--- END ALFRESCO SCRIPT INSTALLER ---"



