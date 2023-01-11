package com.example.jpa.controller;

import com.example.jpa.dto.MemberSearchCondition;
import com.example.jpa.dto.MemberTeamDto;
import com.example.jpa.repository.MemberJpaRepository1;
import com.example.jpa.repository.MemberRep;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberJpaRepository1 rep;
    private final MemberRep rep1;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition) {
        return rep.searchBuilderWhere(condition);
    }

    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
        return rep1.searchPageComplex(condition, pageable);
    }

    @GetMapping("/v3/members")
    public Page<MemberTeamDto> searchMemberV3(MemberSearchCondition condition, Pageable pageable) {
        return rep1.searchPageSimple(condition, pageable);
    }
}
