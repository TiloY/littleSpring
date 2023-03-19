package com.little.v2;

import org.junit.Test;
import org.littlespring.beans.factory.config.TypedStringValue;
import org.littlespring.beans.factory.support.BeanDefinitionValueResolver;
import org.littlespring.beans.factory.support.DefaultBeanFactory;
import org.littlespring.beans.factory.xml.XmlBeanDefinitionReader;
import org.littlespring.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TypedStringValueTest {

    @Test
    public void testResolverTypedStringValue() {
        DefaultBeanFactory factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(new ClassPathResource("petStore-v2.xml"));

        BeanDefinitionValueResolver resolver = new BeanDefinitionValueResolver(factory);

        TypedStringValue stringValue = new TypedStringValue("test");
        Object value = resolver.resolverValueIfNecessary(stringValue);

        assertNotNull(value);
        assertEquals("test", value);
    }
}
