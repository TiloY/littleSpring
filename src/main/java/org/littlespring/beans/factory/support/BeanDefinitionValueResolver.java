package org.littlespring.beans.factory.support;

import org.littlespring.beans.factory.config.RuntimeBeanReference;
import org.littlespring.beans.factory.config.TypedStringValue;

public class BeanDefinitionValueResolver {


    private final DefaultBeanFactory beanFactory;

    public BeanDefinitionValueResolver(DefaultBeanFactory factory) {
        this.beanFactory = factory;
    }

    public Object resolverValueIfNecessary(Object value) {
        if ((value instanceof RuntimeBeanReference)) {
            RuntimeBeanReference ref = (RuntimeBeanReference) value;
            String refBeanName = ref.getBeanName();
            return this.beanFactory.getBean(refBeanName);

        } else if ((value instanceof TypedStringValue)) {
            return ((TypedStringValue) value).getValue();
        }


        throw new RuntimeException("the value " + value + "has not implemented ");
    }
}
