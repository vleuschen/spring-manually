package com;

import com.akm.AppConfig;
import com.akm.service.OrderService;
import com.akm.service.UserService;
import com.spring.AkmApplicationContext;

public class Test {

    public static void main(String[] args) {

        /*
          1、扫描配置类，check哪些类需要被创建
          2、创建非懒加载的bean（单例bean）
         */
        AkmApplicationContext akmApplicationContext = new AkmApplicationContext(AppConfig.class);

        UserService userService = (UserService) akmApplicationContext.getBean("userService");
        userService.test();

        OrderService orderService1 = (OrderService) akmApplicationContext.getBean("orderService");
        OrderService orderService2 = (OrderService) akmApplicationContext.getBean("orderService");
        OrderService orderService3 = (OrderService) akmApplicationContext.getBean("orderService");
        System.out.println(orderService1);
        System.out.println(orderService2);
        System.out.println(orderService3);
    }
}
