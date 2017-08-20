package it.abd.alfresco_allinone_abd.platformsample;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * A demonstration Java controller for the Hello World Web Script.
 *
 * @author martin.bergljung@alfresco.com
 * @since 2.1.0
 */
public class HelloWorldWebScript extends DeclarativeWebScript {
    private static Log logger = LogFactory.getLog(HelloWorldWebScript.class);

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("fromJava", "HelloFromJava");

        logger.debug("Your 'Hello World' Web Script was called!");

        return model;
    }
}