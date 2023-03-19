package com.litte;

import org.junit.Test;
import org.littespring.context.ApplicationContext;
import org.littespring.context.support.ClassPathApplicationContext;
import org.littespring.context.support.FileSystemXmlApplicationContext;
import org.littlespring.service.v1.PetStoreService;

import static org.junit.Assert.assertNotNull;

public class ApplicationContextTest {

    @Test
    public void testGetBean() {
        ApplicationContext ctx = new ClassPathApplicationContext("petStore-v1.xml");
        PetStoreService petStore = (PetStoreService) ctx.getBean("petStore");
        assertNotNull(petStore);
    }

    @Test
    public void testGetBeanFromFileSystemContext() {
        ApplicationContext ctx = new FileSystemXmlApplicationContext("D:\\SANY\\littleSpring\\src\\main\\resources\\petStore-v1.xml");
        PetStoreService petStore = (PetStoreService) ctx.getBean("petStore");
        assertNotNull(petStore);
    }


}
