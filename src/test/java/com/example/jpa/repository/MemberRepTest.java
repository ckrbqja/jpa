package com.example.jpa.repository;

import com.example.jpa.controller.entity.Member;
import com.example.jpa.controller.entity.Team;
import com.example.jpa.dto.MemberSearchCondition;
import com.example.jpa.dto.MemberTeamDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepTest {

    @Autowired
    EntityManager em;
    @Autowired
    MemberRep rep;

    @Test
    public void basic() {
        Member member1 = new Member("member1", 10);
        Member save = rep.save(member1);

        Member member = rep.findById(save.getId()).get();
        assertThat(member1).isEqualTo(member);

        List<Member> all = rep.findAll();
        assertThat(all).containsExactly(member1);


        List<Member> username = rep.findByUsername("member1");
        assertThat(username).containsExactly(member1);

    }

    @Test
    public void test() {

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

        List<MemberTeamDto> memberTeamDtos = rep.search(memberSearchCondition);

        for (MemberTeamDto memberTeamDto : memberTeamDtos) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }

        assertThat(memberTeamDtos).extracting("username").containsExactly("member1","member2","member3","member4");

    }

    @Test
    public void test1() {

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
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberTeamDto> memberTeamDtos = rep.searchPageSimple(memberSearchCondition, pageRequest);

        assertThat(memberTeamDtos.getSize()).isEqualTo(3);
        assertThat(memberTeamDtos.getContent()).extracting("username").containsExactly("member1","member2","member3");

    }

}