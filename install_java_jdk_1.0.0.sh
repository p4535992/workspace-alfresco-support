#search for if any older JDK versions are installed in your system
#rpm -qa | grep -E '^open[jre|jdk]|j[re|dk]'
export JDK_VERSION=8
export TMP_INSTALL=/tmp
echo "--- Verify of the JVM ---"
if [[ $(java -version 2>&1) == *"OpenJDK"* ]]; 
then 
	echo "--- WARNING: You have open jdk installed. Install Java JDK."; 
	ext=rpm
	jdk_version=$JDK_VERSION	
	read -e -p "Install Oracle Java 8${ques} [y/n] " -i "n" installjdk
	if [ "$installjdk" = "y" ]; then
	    echo "Remove of OpenJDK..."
	    yum -y remove java*
		echo "Installing Oracle Java 8. Fetching packages..."
		if [ -n "$1" ]; then
			if [ "$1" == "tar" ]; then
				ext="tar.gz"
			fi
		fi
		readonly url="http://www.oracle.com"
		readonly jdk_download_url1="$url/technetwork/java/javase/downloads/index.html"
		readonly jdk_download_url2=$(curl -s $jdk_download_url1 | egrep -o "\/technetwork\/java/\javase\/downloads\/jdk${jdk_version}-downloads-.+?\.html" | head -1 | cut -d '"' -f 1)
		if [[ -z "$jdk_download_url2" ]]; then 
			echo "Could not get jdk download url - $jdk_download_url1"
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
		echo "--- Finished installing Oracle Java 8"
		echo
	else
	  echo "Skipping install of Oracle Java 8"
	  echo "--- IMPORTANT: You need to install other JDK and adjust paths for the install to be complete"
	  echo
	fi
elif [[ $(java -version 2>&1) == *"command not found"* ]];
then
	echo "--- WARNING: You have no java machine. Install Java JDK."; 
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
		echo "Could not get jdk download url - $jdk_download_url1 with $jdk_download_url2"
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
	echo "--- Finished installing Oracle Java 8"
	echo
else 
	echo '--- You have already a jdk java'; 
fi