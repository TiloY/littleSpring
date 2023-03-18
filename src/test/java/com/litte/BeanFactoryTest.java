package com.litte;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.littlespring.beans.BeanDefinition;
import org.littlespring.beans.factory.BeanCreationException;
import org.littlespring.beans.factory.BeanDefinitionStoreException;
import org.littlespring.beans.factory.support.DefaultBeanFactory;
import org.littlespring.beans.factory.xml.XmlBeanDefinitionReader;
import org.littlespring.service.v1.PetStoreService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @Description :
 * @Author : 田迎
 * @Date : 2023/3/17 22:24
 * @Version : 1.0.0
 **/
public class BeanFactoryTest {

    DefaultBeanFactory factory = null ;
    XmlBeanDefinitionReader reader = null ;
    @Before
    public void setUp(){
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

        reader.loadBeanDefinition("petStore-v1.xml");

        BeanDefinition db = factory.getBeanDefinition("petStore");
        assertEquals("org.littlespring.service.v1.PetStoreService", db.getBeanClassName());
        PetStoreService petStore = (PetStoreService) factory.getBean("petStore");
        assertNotNull(petStore);
    }

    @Test
    public void testInvalidBean() {
        // 1.  根基配置文件 获取bean 的定义
        reader.loadBeanDefinition("petStore-v1.xml");
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
            reader.loadBeanDefinition("xxx.xml");
        } catch (BeanDefinitionStoreException ex) {
          return;
        }

        Assert.fail("expect BeanDefinitionStoreException ");
    }

}
