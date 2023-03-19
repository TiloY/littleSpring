package org.littlespring.beans;

/**
 * bean的定义
 */
public interface BeanDefinition {

    String SCOPE_DEFAULT = "";
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    boolean isSingleton();

    boolean isPrototype();

    String getScope();

    void setScope(String scope);

    String getBeanClassName();
}
