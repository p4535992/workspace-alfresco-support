package fi.vismaconsulting.alfresco.share;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.types.Page;

import java.util.Map;

/**
 * Author: Peter Mikula <peter.mikula@proactum.fi>
 * Created: 3/6/15 11:36 AM
 */
public class PresetsManager extends org.springframework.extensions.surf.PresetsManager {

    private ModelObjectService modelObjectService;

    @Override
    public void setModelObjectService(ModelObjectService modelObjectService) {
        super.setModelObjectService(modelObjectService);
        this.modelObjectService = modelObjectService;
    }

    @Override
    public boolean constructPreset(String id, Map<String, String> tokens) {
        boolean created = super.constructPreset(id, tokens);
        if (!created) {
            // presets might have been initialized from alfresco
            String siteid = tokens.get("siteid");
            Page page = modelObjectService.getPage("site/" + siteid + "/dashboard");
            created = page != null;
        }
        return created;
    }

    static class Installer implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("webframework.presets.manager");
            beanDefinition.setBeanClassName(PresetsManager.class.getName());
        }
    }
}
