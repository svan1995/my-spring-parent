package com.csf.service;

import com.csf.spring.CSFApplicationContext;

public class Test {
    public static void main(String[] args) {
        CSFApplicationContext csfApplicationContext = new CSFApplicationContext(AppConfig.class);
        UserInterface userService = (UserInterface) csfApplicationContext.getBean("userService");
        userService.test();


    }
}
