package com.example.jpa.controller.entity.repository;

import com.example.jpa.controller.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StudyJpaRepTest {
    @Autowired
    StudyRep rep;
    @Test
    public void find() {
        Member m1 = new Member("as");
        Member save = rep.save(m1);
        Member member = rep.findOne(save.getId()).get();

        assertThat(member.getUsername()).isEqualTo(m1.getUsername());

    }

    @Autowired
    StudyJpaRep jpaRep;
    @Test
    public void jpa() {
        Member m1 = new Member("11");
        Member save = jpaRep.save(m1);


        assertThat(m1.getUsername()).isEqualTo(save.getUsername());

    }

}