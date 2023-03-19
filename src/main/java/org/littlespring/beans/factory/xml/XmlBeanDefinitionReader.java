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
import org.littlespring.utils.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class XmlBeanDefinitionReader {

    private static final String ID_ATTRIBUTE = "id";
    private static final String CLASS_ATTRIBUTE = "class";

    private BeanDefinitionRegistry registry;


    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    /**
     * load config  generic BeanDefinition
     *
     * @param config 解析配置文件  这里主要说 xml
     */
    public void loadBeanDefinition(String config) {
        // dom4j解析xml文件
        ClassLoader cl = ClassUtils.getDefaultClassLoader();
        InputStream is = cl.getResourceAsStream(config);
        SAXReader reader = new SAXReader();
        try {
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

        } catch (DocumentException e) {
            throw new BeanDefinitionStoreException("IO Exception parsing XML document .  ");
        }
    }


    public void loadBeanDefinitions(Resource resource) {

        InputStream is = null;
        try {
            is = resource.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SAXReader reader = new SAXReader();
        try {
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

        } catch (DocumentException e) {
            throw new BeanDefinitionStoreException("IO Exception parsing XML document .  ");
        }
    }
}
