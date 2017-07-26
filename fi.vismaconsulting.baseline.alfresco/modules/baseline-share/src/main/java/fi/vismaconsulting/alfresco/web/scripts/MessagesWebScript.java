package fi.vismaconsulting.alfresco.web.scripts;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.json.JSONWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * We prefer not to share our baseline with Alfresco.
 * <p>
 * Author: Peter Mikula <peter.mikula@visma.com>
 * Created: 13.03.2017 13:10
 */
public class MessagesWebScript extends org.springframework.extensions.webscripts.MessagesWebScript {
    /**
     * Generate the message for a given locale.
     *
     * @param locale Java locale format
     * @return messages as JSON string
     * @throws IOException
     */
    @Override
    protected String generateMessages(WebScriptRequest req, WebScriptResponse res, String locale) throws IOException {
        Writer writer = new StringBuilderWriter(8192);
        writer.write("if (typeof Alfresco == \"undefined\" || !Alfresco) {var Alfresco = {};}\r\n");
        writer.write("Alfresco.messages = Alfresco.messages || {global: null, scope: {}}\r\n");
        writer.write("Alfresco.messages.global = ");
        JSONWriter out = new JSONWriter(writer);
        try {
            out.startObject();
            Map<String, String> messages = I18NUtil.getAllMessages(I18NUtil.parseLocale(locale));
            for (Map.Entry<String, String> entry : messages.entrySet()) {
                out.writeValue(entry.getKey(), entry.getValue());
            }
            out.endObject();
        } catch (IOException jsonErr) {
            throw new WebScriptException("Error building messages response.", jsonErr);
        }
        writer.write(";\r\n");
        return writer.toString();
    }

    @Override
    protected String getMessagesPrefix(WebScriptRequest req, WebScriptResponse res, String locale) throws IOException {
        return "if (typeof Alfresco == \"undefined\" || !Alfresco) {var Alfresco = {};}\r\nAlfresco.messages = Alfresco.messages || {global: null, scope: {}}\r\nAlfresco.messages.global = ";
    }

    public static class Install implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

            BeanDefinition definition;
            String prefix = "webscript.org.springframework.extensions.messages";

            definition = beanFactory.getBeanDefinition(prefix + ".get");
            definition.setBeanClassName(MessagesWebScript.class.getName());

            definition = beanFactory.getBeanDefinition(prefix + ".post");
            definition.setBeanClassName(MessagesWebScript.class.getName());
        }
    }
}
