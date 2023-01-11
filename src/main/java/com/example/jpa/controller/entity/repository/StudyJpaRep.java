package com.example.jpa.controller.entity.repository;

import com.example.jpa.controller.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyJpaRep extends JpaRepository<Member, Long> {
}
