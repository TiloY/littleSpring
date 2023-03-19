package org.littlespring.beans.factory.support;

import org.littlespring.beans.BeanDefinition;
import org.littlespring.beans.factory.BeanCreationException;
import org.littlespring.beans.factory.BeanFactory;
import org.littlespring.beans.factory.config.ConfigurableBeanFactory;
import org.littlespring.utils.ClassUtils;
import org.littlespring.utils.ConcurrentReferenceHashMap;

/**
 * DefaultBeanFactory
 * -BeanFactory 的默认实现
 */
public class DefaultBeanFactory implements ConfigurableBeanFactory,BeanDefinitionRegistry {

    private ConcurrentReferenceHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentReferenceHashMap();
    private ClassLoader beanClassLoader;

    public DefaultBeanFactory() {

    }
    @Override
    public void registerBeanDefinition(String beanId, BeanDefinition bd) {
        this.beanDefinitionMap.put(beanId, bd);
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
        ClassLoader cl = this.getBeanClassLoader();
        try {
            Class<?> clz = cl.loadClass(beanClassName);
            return clz.newInstance();
        } catch (Exception e) {
            throw new BeanCreationException("create bean for " + beanClassName + "  failed ", e);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader  = beanClassLoader ;
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return (this.beanClassLoader== null ? ClassUtils.getDefaultClassLoader():beanClassLoader);
    }
}
