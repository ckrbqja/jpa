package com.example.jpa.controller.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Entity @NoArgsConstructor
public class Member {

    public Member(String username) {
        this.username = username;
    }

    @Id
    @GeneratedValue
    private Long id;
    private String username;
}
