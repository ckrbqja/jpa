package com.example.jpa.controller;

import org.apache.catalina.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Gd";
    }

    @GetMapping("/")
    public String root(@AuthenticationPrincipal User user, HttpSession session) {
        System.out.println("user = " + user);
        System.out.println("user = " + user.getUsername());
        System.out.println("session = " + session);
        System.out.println("session = " + session.getId());
        return "Gd";
    }
}
