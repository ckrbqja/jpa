package com.example.jpa.controller.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"team"})
public class Member {

    public Member(String username) {
        this.username = username;
    }

    public Member(String member1, int age, Team team) {
        this.username = member1;
        this.age = age;
        if (team != null) changeTeam(team);
    }

    public Member(String member1, int i) {
        this.username = member1;
        this.age = i;

    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

}
