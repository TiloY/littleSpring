package org.littlespring.beans.factory;

import org.littlespring.beans.BeanDefinition;

public interface BeanFactory {

    Object getBean(String beanId);
}
