package org.littlespring.beans.factory.support;

import org.littlespring.beans.BeanDefinition;

/**
 * 单独抽取一个 读取beanDefinition 的接口
 *  - 用于使得 BeanFactory 接口只专注于 getBean 一个职责
 *  - 解析获取 beanDefinition 交给  @BeanDefinitionRegistry 接口来处理
 */
public interface BeanDefinitionRegistry {

    BeanDefinition getBeanDefinition(String beanId);

    void registerBeanDefinition(String beanId ,BeanDefinition bd);
}
