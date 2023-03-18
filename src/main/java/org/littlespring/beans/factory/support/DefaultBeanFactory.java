package org.littlespring.beans.factory.support;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.littlespring.beans.BeanDefinition;
import org.littlespring.beans.factory.BeanCreationException;
import org.littlespring.beans.factory.BeanDefinitionStoreException;
import org.littlespring.beans.factory.BeanFactory;
import org.littlespring.utils.ClassUtils;
import org.littlespring.utils.ConcurrentReferenceHashMap;

import java.io.InputStream;
import java.util.Iterator;

/**
 * DefaultBeanFactory
 * -BeanFactory 的默认实现
 */
public class DefaultBeanFactory implements BeanFactory {

    private static final String ID_ATTRIBUTE = "id";
    private static final String CLASS_ATTRIBUTE = "class";
    private ConcurrentReferenceHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentReferenceHashMap();

    public DefaultBeanFactory(final String config) {
        this.loadBeanDefinition(config);
    }

    /**
     * load config  generic BeanDefinition
     *
     * @param config 解析配置文件  这里主要说 xml
     */
    private void loadBeanDefinition(String config) {
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
                this.beanDefinitionMap.put(id, bd);
            }

        } catch (DocumentException e) {
            throw new BeanDefinitionStoreException("IO Exception parsing XML document .  ");
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanId) {
        return this.beanDefinitionMap.get(beanId);
    }

    @Override
    public Object getBean(String beanId) {
        BeanDefinition bd = this.getBeanDefinition(beanId);
        if (null == bd) {
            throw new BeanCreationException("Bean Definition does not exist ");
        }
        String beanClassName = bd.getBeanClassName();
        ClassLoader cl = ClassUtils.getDefaultClassLoader();
        try {
            Class<?> clz = cl.loadClass(beanClassName);
            return clz.newInstance();
        } catch (Exception e) {
            throw new BeanCreationException("create bean for " + beanClassName + "  failed ", e);
        }
    }
}
