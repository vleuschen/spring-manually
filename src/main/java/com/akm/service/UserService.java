package com.akm.service;


import com.spring.Component;
import com.spring.Scope;

@Component("userService")
@Scope
public class UserService {

    public void test() {
        System.out.println("[UserService] test...");
    }
}
