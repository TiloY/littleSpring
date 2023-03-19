package com.litte.v1;

import org.junit.Test;
import org.littlespring.context.ApplicationContext;
import org.littlespring.context.support.ClassPathApplicationContext;
import org.littlespring.context.support.FileSystemXmlApplicationContext;
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
