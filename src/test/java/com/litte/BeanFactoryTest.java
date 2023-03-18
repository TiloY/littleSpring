package com.litte;

import org.junit.Test;
import org.littlespring.beans.BeanDefinition;
import org.littlespring.beans.factory.BeanFactory;
import org.littlespring.beans.factory.support.DefaultBeanFactory;
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

    /**
     * 第一个测试用例 、
     *  - 给定一个xml 配置文件（内含bean的定义），能够从中获取：
     *  - 1、 Bean 的定义
     *  - 2、 Bean 的实例
     */
    @Test
    public void testGetBean(){
        // 1.  根基配置文件 获取bean 的定义
        BeanFactory beanFactory = new DefaultBeanFactory("petStore-v1.xml");
        BeanDefinition db  = beanFactory.getBeanDefinition("petStore");
        assertEquals("org.littlespring.service.v1.PetStoreService",db.getBeanClassName());
        PetStoreService petStore = (PetStoreService) beanFactory.getBean("petStore");
        assertNotNull(petStore);
    }

}
