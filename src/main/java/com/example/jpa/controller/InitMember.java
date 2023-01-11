package com.example.jpa.controller;

import com.example.jpa.controller.entity.Member;
import com.example.jpa.controller.entity.Team;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {
    private final InitMemberSev initMemberSev;

    @PostConstruct
    public void init() {
        initMemberSev.init();
    }

    @Component
    static class InitMemberSev{
        @PersistenceContext
        EntityManager em;

        @Transactional
        public void init() {
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);

            for (int i = 0; i < 100; i++) {
                Team team = i % 2 == 0 ? teamA : teamB;
                em.persist(new Member("member" + i, i, team));
            }

        }
    }
}
