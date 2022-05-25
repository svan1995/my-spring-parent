package com.csf.service;

import com.csf.spring.CSFApplicationContext;

public class Test {
    public static void main(String[] args) {
        CSFApplicationContext csfApplicationContext = new CSFApplicationContext(AppConfig.class);
        UserService userService = (UserService) csfApplicationContext.getBean("userService");


    }
}
