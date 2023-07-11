package com.matchUpSports.boundedContext.member.entity;

import com.matchUpSports.boundedContext.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 엔티티 CRUD")
    void memberCrudTest() {
        String username = "testuser";
        String email = "abcd@gmail.com";
        String phoneNumber = "010-1234-5678";
        String authorities = "user";
        String area = "Seoul";
        int tier = 1;

        Member member = Member.builder()
                .username(username)
                .email(email)
                .phoneNumber(phoneNumber)
                .authorities(new HashSet<>())
                .bigDistrict(area)
                .tier(tier)
                .build();
        Member creatingResult = memberRepository.save(member);

        // create
        assertThat(creatingResult.getUsername()).isEqualTo(username);

        // read
        Member readResult = memberRepository.findByUsername(username).get();
        assertThat(readResult.getUsername()).isEqualTo(username);

        // update
        String modifiedUserName = "modifiedUser1";
        Member modifiedMember = readResult.toBuilder()
                .username(modifiedUserName)
                .build();

        Member modifiedMemberResult = memberRepository.save(modifiedMember);
        assertThat(modifiedMemberResult.getUsername()).isEqualTo(modifiedUserName);


        // soft delete
        Member deleteMemberResult = modifiedMemberResult.toBuilder()
                .deleteDate(LocalDateTime.now())
                .build();
        memberRepository.save(deleteMemberResult);

        Optional<Member> afterDelete = memberRepository.findByUsername(modifiedUserName);
        assertThat(afterDelete.isEmpty()).isTrue();
    }
}