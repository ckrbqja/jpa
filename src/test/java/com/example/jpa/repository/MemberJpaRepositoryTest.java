package com.example.jpa.repository;

import com.example.jpa.controller.entity.Member;
import com.example.jpa.controller.entity.Team;
import com.example.jpa.dto.MemberSearchCondition;
import com.example.jpa.dto.MemberTeamDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository1 rep;

    @Test
    public void basicTest() {
        Member member1 = new Member("member1", 10);
        Member save = rep.save(member1);

        Member member = rep.findById(save.getId()).get();
        assertThat(member1).isEqualTo(member);

//        List<Member> all = rep.findAll();
//        assertThat(all).containsExactly(member1);

        List<Member> all = rep.findAll_querydsl();
        assertThat(all).containsExactly(member1);

//        List<Member> username = rep.findByUserName("member1");
//        assertThat(username).containsExactly(member1);

        List<Member> username = rep.findByUser_querydsl("member1");
        assertThat(username).containsExactly(member1);
    }

    @Test
    public void searchTest() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition memberSearchCondition = new MemberSearchCondition();
//        memberSearchCondition.setAgeGoe(35);
//        memberSearchCondition.setAgeLoe(40);
//        memberSearchCondition.setTeamName("teamB");

        List<MemberTeamDto> memberTeamDtos = rep.searchBuilder(memberSearchCondition);

        for (MemberTeamDto memberTeamDto : memberTeamDtos) {
            System.out.println("memberTeamDto = " + memberTeamDto);

        }

        assertThat(memberTeamDtos).extracting("username").containsExactly("member3","member4");


    }
}