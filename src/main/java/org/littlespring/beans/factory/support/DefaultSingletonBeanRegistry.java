package org.littlespring.beans.factory.support;

import org.littlespring.beans.factory.config.SingletonBeanRegistry;
import org.littlespring.utils.Assert;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    private ConcurrentHashMap<Object, Object> singletonObjects = new ConcurrentHashMap<>(64);

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        Assert.notNull(beanName, " 'beanName' must not be null ");
        Object oldObject = this.getSingleton(beanName);
        if (oldObject != null) {
            throw new IllegalStateException("could not register object [ " + singletonObject +
                    "] under bean name ' " + beanName + " ' : there is already object [" + oldObject + "]");
        }
        this.singletonObjects.put(beanName,singletonObject);
    }

    @Override
    public Object getSingleton(String beanName) {
        return this.singletonObjects.get(beanName);
    }
}
