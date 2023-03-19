package com.litte.v1;

import org.junit.Assert;
import org.junit.Test;
import org.littlespring.core.io.ClassPathResource;
import org.littlespring.core.io.FileSystemResource;
import org.littlespring.core.io.Resource;

import java.io.InputStream;

public class ResourceTest {



    @Test
    public void testClassPathResource() throws Exception{

            Resource r =   new ClassPathResource("petStore-v1.xml");

            InputStream is  = null ;
            try {
                is = r.getInputStream();
                Assert.assertNotNull(is);
            }finally {
                if(null != is){
                    is.close();
                }
            }
    }


    @Test
    public void testFileSystemResource() throws Exception{
        Resource r =   new FileSystemResource("D:\\SANY\\littleSpring\\src\\main\\resources\\petStore-v1.xml");

        InputStream is  = null ;
        try {
            is = r.getInputStream();
            Assert.assertNotNull(is);
        }finally {
            if(null != is){
                is.close();
            }
        }
    }
}
