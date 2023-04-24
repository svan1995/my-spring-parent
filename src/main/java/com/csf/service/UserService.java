package com.csf.service;

import com.csf.spring.Autowired;
import com.csf.spring.BeanNameAware;
import com.csf.spring.Component;
import com.csf.spring.InitializingBean;

@Component
public class UserService implements BeanNameAware, InitializingBean, UserInterface {

    @Autowired
    private OrderService orderService;

    private String beanName;

    public void test1() {
        System.out.println(orderService);

    }


    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void afterPropertiesSet() {
        System.out.println(beanName + ": --------- afterPropertiesSet");
    }

    public void test() {
        System.out.println("hhh");
    }
}
