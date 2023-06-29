package com.matchUpSports.boundedContext.member.service;

import com.matchUpSports.base.Role;
import com.matchUpSports.base.rsData.RsData;
import com.matchUpSports.base.security.social.inter.DivideOAuth2User;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }
    @Transactional
    public Member saveOAuth2Member(DivideOAuth2User customOAuth2User) {
        Optional<Member> findByName = memberRepository.findByUsername(customOAuth2User.getUsername());

        if (findByName.isEmpty()) {
            Member member = Member.builder()
                    .username(customOAuth2User.getUsername())
                    .email(customOAuth2User.getEmail())
                    .build();
            member.addRole(Role.USER);

            return memberRepository.save(member);
        }

        return findByName.get();
    }
}

