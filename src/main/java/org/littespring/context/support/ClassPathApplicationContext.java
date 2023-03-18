package org.littespring.context.support;

import org.littespring.context.ApplicationContext;
import org.littlespring.beans.factory.support.DefaultBeanFactory;
import org.littlespring.beans.factory.xml.XmlBeanDefinitionReader;

public class ClassPathApplicationContext implements ApplicationContext {
   private DefaultBeanFactory factory   = null ;

    public ClassPathApplicationContext(String config) {
        this.factory  = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader  = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinition(config);
    }

    @Override
    public Object getBean(String beanId) {
        return factory.getBean(beanId);
    }
}
