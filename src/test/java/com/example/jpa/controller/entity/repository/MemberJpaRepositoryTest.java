package com.example.jpa.controller.entity.repository;

import com.example.jpa.controller.entity.Member;
import com.example.jpa.controller.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
//@Rollback(false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void Member() {
        Member member = new Member("memberA");
        Member save = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(save.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    public void Member1() {
        Member member = new Member("memberA");
        Member save = memberRepository.save(member);

        Member findMember = memberRepository.findById(save.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    public void BasicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        Member save1 = memberJpaRepository.save(member1);
        Member save2 = memberJpaRepository.save(member2);


        Member member3 = memberJpaRepository.findById(save1.getId()).get();
        Member member4 = memberJpaRepository.findById(save2.getId()).get();

        assertThat(member3).isEqualTo(member1);
        assertThat(member4).isEqualTo(member2);


        List<Member> all = memberJpaRepository.findAll();
//        assertThat(all.size()).isEqualTo(2);


    }

    @Autowired
    EntityManager em;
    @Test
    public void querydsl() {
        Member a = new Member("a");
        em.persist(a);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QMember qm = QMember.member;
        Member member = query.selectFrom(qm).fetchOne();
        Assertions.assertThat(member).isEqualTo(member);
    }
}