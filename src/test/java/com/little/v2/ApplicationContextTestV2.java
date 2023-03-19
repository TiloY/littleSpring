package com.little.v2;

import org.junit.Test;
import org.littlespring.context.ApplicationContext;
import org.littlespring.context.support.ClassPathApplicationContext;
import org.littlespring.service.v2.PetStoreService;

import static org.junit.Assert.assertNotNull;

public class ApplicationContextTestV2 {


    @Test
    public void testBeanProperty(){
        ApplicationContext ctx = new ClassPathApplicationContext("petStore-v2.xml");
        PetStoreService petStore  = (PetStoreService)ctx.getBean("petStore");

        assertNotNull(petStore.getAccountDao());
        assertNotNull(petStore.getItemDao());
    }
}
