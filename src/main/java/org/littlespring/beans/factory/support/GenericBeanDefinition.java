package org.littlespring.beans.factory.support;

import org.littlespring.beans.BeanDefinition;

public class GenericBeanDefinition implements BeanDefinition {

    private String beanClassName;
    private String id;
    private boolean singleton = true;
    private boolean prototype = false;
    private String scope = SCOPE_DEFAULT;


    public GenericBeanDefinition(String id, String beanClassName) {
        this.id = id;
        this.beanClassName = beanClassName;
    }

    @Override
    public String getBeanClassName() {
        return beanClassName;
    }

    @Override
    public boolean isSingleton() {
        return this.singleton;
    }

    @Override
    public boolean isPrototype() {
        return this.prototype;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
        this.singleton  = SCOPE_SINGLETON.equals(scope)|| SCOPE_DEFAULT.equals(scope);
        this.prototype  = SCOPE_PROTOTYPE.equals(scope);
    }
}
