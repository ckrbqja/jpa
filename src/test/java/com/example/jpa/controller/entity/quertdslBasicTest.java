package com.example.jpa.controller.entity;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.example.jpa.controller.entity.QMember.*;
import static com.example.jpa.controller.entity.QTeam.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class quertdslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

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
    }


    @Test
    public void startJPQL() {
        Member singleResult = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1").getSingleResult();

        assertThat(singleResult.getAge()).isEqualTo(10);
    }

    @Test
    public void querydsl() {

        Member member1 = queryFactory.selectFrom(member).where(member.username.eq("member1")).fetchOne();
        assertThat(member1.getAge()).isEqualTo(10);
    }

    @Test
    public void search() {
        Member member1 = queryFactory.selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10))).fetchOne();

        assertThat(member1.getUsername()).isEqualTo("member1");

    }

    @Test
    public void searchWithParam() {
        Member member1 = queryFactory.selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10))
                .fetchOne();

        assertThat(member1.getUsername()).isEqualTo("member1");

    }

    @Test
    public void resultFetrch() {
        JPAQuery<Member> jpa = queryFactory.selectFrom(member);

        List<Member> fetch = jpa.fetch();
        Member member1 = jpa.fetchOne();
        Member member2 = jpa.fetchFirst();
        QueryResults<Member> memberQueryResults = jpa.fetchResults();
        List<Member> results = memberQueryResults.getResults();
        long l = jpa.fetchCount();

    }

    @Test
    public void sort() {
        em.persist(new Member(null, 100, null));
        em.persist(new Member("member5", 100, null));
        em.persist(new Member("member6", 100, null));

        List<Member> fetch = queryFactory.selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast()).fetch();

        Member member5 = fetch.get(0);
        Member member6 = fetch.get(1);
        Member memberNull = fetch.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    public void pageing1() {
        List<Member> fetch = queryFactory.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        assertThat(fetch.size()).isEqualTo(2);

    }

    @Test
    public void aggreation() {
        List<Tuple> fetch = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = fetch.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.avg())).isEqualTo(100);
        assertThat(tuple.get(member.age.max())).isEqualTo(25);
    }

    @Test
    @DisplayName("팀의 이름과 각 팀의 평균을 구하라")
    public void group() {
        queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();
    }

    @Test
    @DisplayName("팀 A에 소속된 모든 회원")
    public void join() {
        List<Member> memberA = queryFactory.selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        System.out.println("memberA = ");
        memberA.stream().map(Member::getUsername).forEach(System.out::print);

        assertThat(memberA).extracting("username").contains("member1, member2");
    }

    @Test
    public void theta_join() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Member> fetch = queryFactory.select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(fetch).extracting("username")
                .containsExactly("teamA", "teamB");
    }

    @Test
    public void join_on_filtering() {
        List<Tuple> teamA = queryFactory.select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();

        teamA.forEach(System.out::println);
    }

    @Test
    @DisplayName("연관관계가 없는 조인")
    public void join_on_no_relation() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> fetch = queryFactory.select(team, member)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        fetch.forEach(System.out::println);
    }
}