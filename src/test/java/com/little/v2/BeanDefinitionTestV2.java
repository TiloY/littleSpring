package com.little.v2;

import org.junit.Assert;
import org.junit.Test;
import org.littlespring.beans.BeanDefinition;
import org.littlespring.beans.PropertyValue;
import org.littlespring.beans.factory.config.RuntimeBeanReference;
import org.littlespring.beans.factory.support.DefaultBeanFactory;
import org.littlespring.beans.factory.xml.XmlBeanDefinitionReader;
import org.littlespring.core.io.ClassPathResource;

import java.util.List;

public class BeanDefinitionTestV2 {


    @Test
    public void testBeanDefinition() {
        DefaultBeanFactory factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(new ClassPathResource("petStore-v2.xml"));
        BeanDefinition bd = factory.getBeanDefinition("petStore");

        List<PropertyValue> pvs = bd.getPropertyValues();
        Assert.assertTrue(pvs.size() == 2);

        {
            PropertyValue pv = this.getPropertyValue("accountDao",pvs);
            Assert.assertNotNull(pv);
            Assert.assertTrue(pv.getValue() instanceof RuntimeBeanReference);
        }

        {
            PropertyValue pv = this.getPropertyValue("itemDao",pvs);
            Assert.assertNotNull(pv);
            Assert.assertTrue(pv.getValue() instanceof RuntimeBeanReference);
        }

    }

    private PropertyValue getPropertyValue(String name, List<PropertyValue> pvs) {
        for (PropertyValue pv : pvs) {
           if(name.equals(pv.getName())){
            return pv ;
           }
        }
        return null;
    }
}
