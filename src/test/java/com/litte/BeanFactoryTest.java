package com.litte;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.littlespring.beans.BeanDefinition;
import org.littlespring.beans.factory.BeanCreationException;
import org.littlespring.beans.factory.BeanDefinitionStoreException;
import org.littlespring.beans.factory.support.DefaultBeanFactory;
import org.littlespring.beans.factory.xml.XmlBeanDefinitionReader;
import org.littlespring.core.io.ClassPathResource;
import org.littlespring.service.v1.PetStoreService;

import static org.junit.Assert.*;

/**
 * @Description :
 * @Author : 田迎
 * @Date : 2023/3/17 22:24
 * @Version : 1.0.0
 **/
public class BeanFactoryTest {

    DefaultBeanFactory factory = null;
    XmlBeanDefinitionReader reader = null;

    @Before
    public void setUp() {
        factory = new DefaultBeanFactory();
        reader = new XmlBeanDefinitionReader(factory);
    }

    /**
     * 第一个测试用例 、
     * - 给定一个xml 配置文件（内含bean的定义），能够从中获取：
     * - 1、 Bean 的定义
     * - 2、 Bean 的实例
     */
    @Test
    public void testGetBean() {
        // 1.  根基配置文件 获取bean 的定义
        // "petStore-v1.xml"
        ClassPathResource resource = new ClassPathResource("petStore-v1.xml");
        reader.loadBeanDefinitions(resource);

        BeanDefinition bd = factory.getBeanDefinition("petStore");

        assertTrue(bd.isSingleton());
        assertFalse(bd.isPrototype());
        assertEquals(BeanDefinition.SCOPE_DEFAULT, bd.getScope());

        assertEquals("org.littlespring.service.v1.PetStoreService", bd.getBeanClassName());
        PetStoreService petStore = (PetStoreService) factory.getBean("petStore");
        assertNotNull(petStore);

        PetStoreService petStore1 = (PetStoreService) factory.getBean("petStore");
        assertTrue(petStore.equals(petStore1));

    }

    @Test
    public void testInvalidBean() {
        // 1.  根基配置文件 获取bean 的定义
        ClassPathResource resource = new ClassPathResource("petStore-v1.xml");
        reader.loadBeanDefinitions(resource);
        try {
            factory.getBean("invalidBean");
        } catch (BeanCreationException ex) {
            return;
        }

        Assert.fail("expect BeanCreationException ");
    }

    @Test
    public void testInvalidXML() {
        try {
            ClassPathResource resource = new ClassPathResource("xxx.xml");
            reader.loadBeanDefinitions(resource);
        } catch (BeanDefinitionStoreException ex) {
            return;
        }

        Assert.fail("expect BeanDefinitionStoreException ");
    }

}
