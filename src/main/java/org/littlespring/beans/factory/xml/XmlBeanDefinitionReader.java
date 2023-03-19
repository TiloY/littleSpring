package org.littlespring.beans.factory.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.littlespring.beans.BeanDefinition;
import org.littlespring.beans.factory.BeanDefinitionStoreException;
import org.littlespring.beans.factory.support.BeanDefinitionRegistry;
import org.littlespring.beans.factory.support.GenericBeanDefinition;
import org.littlespring.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class XmlBeanDefinitionReader {

    private static final String ID_ATTRIBUTE = "id";
    private static final String CLASS_ATTRIBUTE = "class";
    private static final String SCOPE_ATTRIBUTE = "scope";

    private BeanDefinitionRegistry registry;


    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }


    public void loadBeanDefinitions(Resource resource) {

        InputStream is = null;
        try {
            is = resource.getInputStream();

            SAXReader reader = new SAXReader();
            Document doc = reader.read(is);
            Element root = doc.getRootElement();

            Iterator it = root.elementIterator();
            while (it.hasNext()) {
                Element ele = (Element) it.next();
                String id = ele.attributeValue(ID_ATTRIBUTE);
                String beanClassName = ele.attributeValue(CLASS_ATTRIBUTE);
                BeanDefinition bd = new GenericBeanDefinition(id, beanClassName);
                this.registry.registerBeanDefinition(id, bd);
            }

        } catch (Exception e) {
            throw new BeanDefinitionStoreException("IO Exception parsing XML document .  ");
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
