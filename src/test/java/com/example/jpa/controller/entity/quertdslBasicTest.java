package com.example.jpa.controller.entity;

import com.example.jpa.dto.MemberDto;
import com.example.jpa.dto.QMemberDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

import java.util.List;

import static com.example.jpa.controller.entity.QMember.*;
import static com.example.jpa.controller.entity.QTeam.*;
import static com.querydsl.jpa.JPAExpressions.*;
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

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchNo() {
        em.flush();
        em.clear();

        Member member1 = queryFactory.selectFrom(member).where(member.username.eq("member1")).fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(member1.getTeam());

        assertThat(loaded).as("페치 조인 미적용").isFalse();

    }

    @Test
    public void fetchJoin() {
        em.flush();
        em.clear();

        Member member1 = queryFactory.selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1")).fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(member1.getTeam());

        assertThat(loaded).as("페치 조인").isTrue();

    }

    @Test
    @DisplayName("나이가 가장 많은 사람 조회")
    public void subQuery() {
        QMember qmem = new QMember("qmem");

        List<Member> fetch = queryFactory.selectFrom(member)
                .where(member.age.eq(select(qmem.age.max()).from(qmem)))
                .fetch();
        assertThat(fetch).extracting("age").containsExactly(40);

    }

    @Test
    public void selectSub() {
        QMember qmem = new QMember("qmem");

        List<Tuple> fetch = queryFactory.select(
                member.username,
                select(qmem.age.max()).from(qmem)
        ).from(member).fetch();

        fetch.forEach(System.out::println);

    }

    @Test
    public void basicCase() {
        List<String> fetch = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("zz"))
                .from(member)
                .fetch();

        fetch.forEach(System.out::println);

    }

    @Test
    public void complexCase() {
        List<String> 기타 = queryFactory.select(
                        new CaseBuilder()
                                .when(member.age.between(0, 20)).then("20대")
                                .when(member.age.between(21, 30)).then("30대")
                                .otherwise("기타")
                )
                .from(member)
                .fetch();
        기타.forEach(System.out::println);

    }

    @Test
    public void constance() {
        List<Tuple> a = queryFactory
                .select(
                        member.username,
                        Expressions.constant("A")
                ).from(member)
                .fetch();

        a.forEach(System.out::println);
    }
    @Test
    public void concat() {
        List<String> fetch = queryFactory.select(
                        member.username.concat("_").concat(member.username.stringValue())
                ).from(member)
                .fetch();

        fetch.forEach(System.out::println);
    }

    @Test
    public void findByDtoJPA() {
        List<MemberDto> resultList = em.createQuery("select new com.example.jpa.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class).getResultList();

        resultList.forEach(System.out::println);
    }

    @Test
    public void findSetter() {
        List<MemberDto> fetch = queryFactory
                .select(Projections.constructor(MemberDto.class
                        , member.username
                        , member.age))
                .from(member)
                .fetch();

        fetch.forEach(System.out::println);
    }

    @Test
    public void findDtoByQueryProjection() {
        List<MemberDto> fetch = queryFactory.select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();

        fetch.forEach(System.out::println);
    }

    @Test
    public void dynamic_BooleanBuilder() {
        String member1 = "member1";
        Integer ageParm = 10;

        List<Member> members = searchMember1(member1, ageParm);
        members.forEach(System.out::println);


    }
    @Test
    public void dynamic_BooleanBuilder_where() {
        String member1 = "member1";
        Integer ageParm = 10;

        List<Member> members = searchMember2(member1, ageParm);
        members.forEach(System.out::println);


    }

    private List<Member> searchMember1(String member1, Integer ageParm) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (member1 != null) {
            booleanBuilder.and(member.username.eq(member1));
        }

        if (ageParm != null) {
            booleanBuilder.and(member.age.eq(ageParm));
        }

        return queryFactory.selectFrom(member)
                .where(booleanBuilder)
                .fetch();
    }

    private List<Member> searchMember2(String member1, Integer ageParm) {
        return queryFactory.selectFrom(member)
//                .where(userNameEq(member1), ageParmEq(ageParm))
                .where(allEq(member1, ageParm))
                .fetch();
    }

    private BooleanExpression ageParmEq(Integer ageParm) {
        return ageParm == null ? null : member.age.eq(ageParm);
    }

    private BooleanExpression userNameEq(String member1) {
        return member1 == null ? null : member.username.eq(member1);
    }

    private BooleanExpression allEq(String username, Integer agecond) {
        return userNameEq(username).and(ageParmEq(agecond));
    }

    @Test
    public void bulkUpdate() {
        long 비회원 = queryFactory.update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();

        em.flush();
        em.clear();
        queryFactory.selectFrom(member).fetch().forEach(System.out::println);

    }

    @Test
    public void bulkAdd() {
        long execute = queryFactory.update(member)
                .set(member.age, member.age.multiply(1))
                .execute();
    }

    @Test
    public void bulkDelete() {
        queryFactory.delete(member)
                .where(member.age.lt(18))
                .execute();
    }

    @Test
    public void sqlFunction() {
        queryFactory.select(
                        Expressions.stringTemplate(
                                "function('replace', {0}, {1}, {2})",
                                member.username, "member", "M"
                        )
                ).from(member)
                .fetch()
                .forEach(System.out::println);
    }
}