package com.csf.service;

import com.csf.spring.BeanPostProcessor;
import com.csf.spring.Component;

@Component
public class CSFBeanPostProcessor implements BeanPostProcessor {
    public void postProcessBeforeInitialization(String beanName, Object bean) {
        System.out.println(beanName + " ---- before initialization");
    }

    public void postProcessAfterInitialization(String beanName, Object bean) {
        System.out.println(beanName + " ---- after initialization");
    }
}
