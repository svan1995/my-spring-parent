package com.csf.spring;

public interface BeanPostProcessor {

    public void postProcessBeforeInitialization(String beanName, Object bean);

    public void postProcessAfterInitialization(String beanName, Object bean);


}
