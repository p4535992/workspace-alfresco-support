## Alfresco Baseline 5.2

### Prerequisites

This project requires special `baseline-alfresco` profile in your `$HOME/.m2/settings.xml`

```xml
<profiles>
   <profile>
        <id>baseline-alfresco</id>
        <repositories>
            <repository>
                <id>nexus.vismaconsulting.fi_baseline-alfresco</id>
                <url>https://nexus.vismaconsulting.fi/repository/baseline-alfresco</url>
            </repository>
        </repositories>
   </profile>
</profiles>
```
that needs to be activated by every maven command

```bash
$ mvn -Pbaseline-alfresco params...
```

alternatively you can use the wrapper

```bash
$ ./mvn.sh params...
```

### Build structure

Alfresco Baseline is composed of 

- parent: common parent for sdk modules
- sdk-repo: parent for alfresco repository extensions
- sdk-share: parent for alfresco share extensions
- platform: alfresco baseline platform builder
- modules/*: alfresco baseline provided extensions

Additionally there are two build aggregator modules:

- .: root aggregator
- modules: modules aggregator

Aggregator modules are there **ONLY FOR IMPORTING PROJECT TO IDEA - NOT TO INSTALL OR DEPLOY**

If you need to do modifications then

- check current platform version (e.g. 5.2.d.3)
- identify modules to be changed and their versions
- upgrade **platform** and module versions to platform version+1 (e.g. 5.2.d.4)
- do you modifications and test them
- commit and push
- deploy **manually** changed modules and platform 
```bash
$ ./deploy -f modules/CHANGED_MODULE/pom.xml
$ ./deploy -f platform/pom.xml
```

- update wiki: alfresco releases

