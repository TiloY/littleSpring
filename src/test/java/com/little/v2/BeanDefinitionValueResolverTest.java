package com.little.v2;

import org.junit.Test;
import org.littlespring.beans.factory.config.RuntimeBeanReference;
import org.littlespring.beans.factory.support.BeanDefinitionValueResolver;
import org.littlespring.beans.factory.support.DefaultBeanFactory;
import org.littlespring.beans.factory.xml.XmlBeanDefinitionReader;
import org.littlespring.core.io.ClassPathResource;
import org.littlespring.dao.v2.AccountDao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BeanDefinitionValueResolverTest {

    @Test
    public void testResolverRuntimeBeanReferenceTest() {
        DefaultBeanFactory factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(new ClassPathResource("petStore-v2.xml"));

        BeanDefinitionValueResolver resolver = new BeanDefinitionValueResolver(factory);

        RuntimeBeanReference reference = new RuntimeBeanReference("accountDao");
        Object value = resolver.resolverValueIfNecessary(reference);

        assertNotNull(value);
        assertTrue(value instanceof AccountDao);
    }

}
