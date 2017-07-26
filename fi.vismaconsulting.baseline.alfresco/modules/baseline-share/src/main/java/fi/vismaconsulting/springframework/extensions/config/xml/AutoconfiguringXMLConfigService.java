package fi.vismaconsulting.springframework.extensions.config.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.*;
import org.dom4j.CharacterData;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.extensions.config.ConfigSection;
import org.springframework.extensions.config.ConfigSource;
import org.springframework.extensions.config.evaluator.Evaluator;
import org.springframework.extensions.config.xml.XMLConfigService;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * XMLConfigService implementation that processes the placeholders.
 * <p>
 * Author: Peter Mikula <peter.mikula@proactum.fi>
 * Created: 6/26/13 9:53 AM
 */
public class AutoconfiguringXMLConfigService extends XMLConfigService {

    private static Log logger = LogFactory.getLog(AutoconfiguringXMLConfigService.class);

    private final Properties properties;

    public AutoconfiguringXMLConfigService(ConfigSource configSource, Properties properties) {
        super(configSource);
        this.properties = properties;

        // do we have an endpoint url?
        String port = System.getProperty("autoconf.server.port");
        String endpointUrl = properties.getProperty("alfresco.endpoint.url");
        if (!StringUtils.hasLength(endpointUrl) && StringUtils.hasLength(port)) {
            String host = System.getProperty("autoconf.server.host", "localhost");
            endpointUrl = String.format("http://%s:%s/alfresco", host, port);
            logger.info("Setting alfresco endpoint url to " + endpointUrl);
            properties.put("alfresco.endpoint.url", endpointUrl);
        }
    }

    @Override
    public String parseFragment(Element rootElement, Map<String, ConfigElementReader> parsedElementReaders,
                                Map<String, Evaluator> parsedEvaluators, List<ConfigSection> parsedConfigSections) {
        fixDefaultEndpoints(rootElement);
        replacePlaceholders(rootElement);
        return super.parseFragment(rootElement, parsedElementReaders, parsedEvaluators, parsedConfigSections);
    }

    protected void fixDefaultEndpoints(Element rootElement) {
        final String prefix = "http://localhost:8080/alfresco/";
        final String xpath = "/app-config/config[@condition='Remote']/remote/endpoint/endpoint-url";
        for (Node node : (List<Node>) rootElement.selectNodes(xpath)) {
            String text = node.getText();
            if (text != null && text.startsWith(prefix)) {
                node.setText(text.replace(prefix, "${alfresco.endpoint.url}/"));
            }
        }
    }

    protected void replacePlaceholders(Element rootElement) {
        rootElement.accept(new VisitorSupport() {

            private PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(
                    PropertyPlaceholderConfigurer.DEFAULT_PLACEHOLDER_PREFIX,
                    PropertyPlaceholderConfigurer.DEFAULT_PLACEHOLDER_SUFFIX);

            @Override
            public void visit(Text node) {
                processNode(node);
            }

            @Override
            public void visit(CDATA node) {
                processNode(node);
            }

            private void processNode(CharacterData node) {
                String text = node.getText();
                if (text != null) {
                    text = helper.replacePlaceholders(text, properties);
                    node.setText(text);
                }
            }
        });
    }

    public static class Install implements BeanFactoryPostProcessor {

        private final Properties properties;

        public Install(Properties properties) {
            this.properties = properties;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            BeanDefinition definition = beanFactory.getBeanDefinition("web.config");
            definition.setBeanClassName(AutoconfiguringXMLConfigService.class.getName());
            definition.getConstructorArgumentValues().addGenericArgumentValue(properties);
        }
    }
}
