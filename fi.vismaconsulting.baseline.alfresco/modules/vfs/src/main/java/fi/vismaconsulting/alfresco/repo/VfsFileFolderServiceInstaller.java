package fi.vismaconsulting.alfresco.repo;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;

/**
 * Author: Peter Mikula <peter.mikula@proactum.fi>
 * Created: 5/4/15 9:32 AM
 */
public class VfsFileFolderServiceInstaller implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition definition = beanFactory.getBeanDefinition("webDAVHelper");
        definition.getPropertyValues().add("fileFolderService", new RuntimeBeanReference("VfsFileFolderService"));
        definition = beanFactory.getBeanDefinition("webdavService");
        definition.getPropertyValues().add("fileFolderService", new RuntimeBeanReference("VfsFileFolderService"));
    }
}
