package com.litte;

import org.junit.Test;
import org.littespring.context.ApplicationContext;
import org.littespring.context.support.ClassPathApplicationContext;
import org.littlespring.service.v1.PetStoreService;

import static org.junit.Assert.assertNotNull;

public class ApplicationContextTest {

   @Test
    public void testGetBean(){
       ApplicationContext ctx = new ClassPathApplicationContext("petStore-v1.xml");
       PetStoreService petStore = (PetStoreService) ctx.getBean("petStore");
       assertNotNull(petStore);
   }
}
