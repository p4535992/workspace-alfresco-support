package fi.vismaconsulting.alfresco.solr4;

import fi.vismaconsulting.platform.utils.tomcat.AutoConfigurer;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Author: Peter Mikula <peter.mikula@visma.com>
 * Created: 7/30/15 12:40 PM
 */
public class PlatformConfigurer implements AutoConfigurer {

    @Override
    public void configure(ServletContext context) {

        if ("false".equals(System.getProperty("autoconf.solr"))) {
            return;
        }

        File catalinaBase = getCatalinaBase();
        if (catalinaBase == null) {
            // not on tomcat ?
            return;
        }

        File solrHome = getSolrHome();
        if (solrHome == null) {
            solrHome = new File(catalinaBase, "../solr4");
            if (solrHome.exists()) {
                try {
                    solrHome = solrHome.getCanonicalFile();
                } catch (IOException e) {
                    throw new RuntimeException(solrHome.getPath(), e);
                }
                System.setProperty("solr.solr.home", solrHome.getPath());

                System.setProperty("solr.solr.model.dir",
                        String.format("%s/%s", solrHome.getPath(), "data/model"));
                System.setProperty("solr.solr.content.dir",
                        String.format("%s/%s", solrHome.getPath(), "data/content"));

                // configure cores ...
                for (String coreName : Arrays.asList("archive-SpacesStore", "workspace-SpacesStore")) {
                    String host = System.getProperty("autoconf.server.host", "localhost");
                    String port = System.getProperty("autoconf.server.port", "8080");
                    fixCoreConfig(solrHome, coreName, host, port);
                }

                fixLoggingDest(solrHome);
            }
        }
    }

    protected File getCatalinaBase() {
        String base = System.getProperty("catalina.base");
        if (base == null) {
            return null;
        }
        File baseDir = new File(base);
        return baseDir.isDirectory() ? baseDir : null;
    }

    protected File getSolrHome() {
        String home = null;
        try {
            Context c = new InitialContext();
            home = (String) c.lookup("java:comp/env/solr/home");
        } catch (Exception e) {
            //...
        }

        if (home == null) {
            String prop = "solr.solr.home";
            home = System.getProperty(prop);
        }

        return home == null ? null : new File(home);
    }

    protected void fixCoreConfig(File solrHome, String coreName, String host, String port) {
        File newCore = new File(solrHome, coreName);
        File config = new File(newCore, "conf/solrcore.properties");
        if (!config.isFile()) {
            return;
        }

        // see AlfrescoCoreAdminHandler
        try {
            PropertiesConfiguration props = new PropertiesConfiguration(config);

            boolean modified = false;
            modified |= setProperty(props, "data.dir.root",
                    String.format("%s/data/index", solrHome.getPath()));

            modified |= setProperty(props, "alfresco.host", host);
            modified |= setProperty(props, "alfresco.port", port);
            modified |= setProperty(props, "alfresco.baseUrl", "/alfresco");

            if (modified) {
                props.save();
            }

        } catch (ConfigurationException e) {
            throw new RuntimeException(newCore.getPath(), e);
        }
    }

    protected void fixLoggingDest(File solrHome) {
        File config = new File(solrHome, "log4j-solr.properties");
        if (!config.isFile()) {
            return;
        }

        try {
            PropertiesConfiguration props = new PropertiesConfiguration(config);
            String location = props.getString("log4j.appender.File.File");
            if ("solr.log".equals(location)) {
                props.setProperty("log4j.appender.File", "org.apache.log4j.RollingFileAppender");
                props.setProperty("log4j.appender.File.File", "${catalina.base}/logs/alfresco-solr4.log");
                props.setProperty("log4j.appender.File.MaxFileSize", "100MB");
                props.setProperty("log4j.appender.File.MaxBackupIndex", "0");
                props.setProperty("log4j.appender.File.layout", "org.apache.log4j.PatternLayout");
                props.setProperty("log4j.appender.File.layout.ConversionPattern", "%d{ISO8601} %x %-5p [%c] [%t] %m%n");
                props.save();
            }
        } catch (ConfigurationException e) {
            throw new RuntimeException(config.getPath(), e);
        }
    }


    protected boolean setProperty(PropertiesConfiguration props, String name, String value) {
        Object oldValue = props.getProperty(name);
        props.setProperty(name, value);
        return !value.equals(oldValue);
    }
}
