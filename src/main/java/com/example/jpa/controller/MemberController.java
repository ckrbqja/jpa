package com.example.jpa.controller;

import com.example.jpa.controller.entity.Member;
import com.example.jpa.dto.MemberSearchCondition;
import com.example.jpa.dto.MemberTeamDto;
import com.example.jpa.repository.MemberJpaRepository1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberJpaRepository1 rep;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition) {
        return rep.searchBuilderWhere(condition);
    }
}
