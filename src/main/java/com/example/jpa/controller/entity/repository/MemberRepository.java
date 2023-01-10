package com.example.jpa.controller.entity.repository;

import com.example.jpa.controller.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
