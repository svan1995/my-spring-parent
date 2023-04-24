package com.csf.service;

import com.csf.spring.BeanPostProcessor;
import com.csf.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class CSFBeanPostProcessor implements BeanPostProcessor {
    public Object postProcessBeforeInitialization(String beanName, Object bean) {
        System.out.println(beanName + " ---- before initialization");
        return bean;
    }

    public Object postProcessAfterInitialization(String beanName, final Object bean) {
        System.out.println(beanName + " ---- after initialization");
        if ("userService".equals(beanName)) {
            Object proxyInstance = Proxy.newProxyInstance(CSFBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("切面逻辑");
                    return method.invoke(bean, args);
                }
            });
            return proxyInstance;
        }
        return bean;
    }
}
