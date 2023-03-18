package org.littlespring.beans.factory.support;

import org.littlespring.beans.BeanDefinition;

public class GenericBeanDefinition implements BeanDefinition {

    private String beanClassName;
    private String id;

    public GenericBeanDefinition(String id, String beanClassName) {
        this.id = id;
        this.beanClassName = beanClassName;
    }

    @Override
    public String getBeanClassName() {
        return beanClassName;
    }
}
