package com.example.jpa.repository;

import com.example.jpa.controller.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRep extends JpaRepository<Member, Long>, MemberRepCustom {
    List<Member> findByUsername(String username);
}
