package com.matchUpSports.base.security.form;

import com.matchUpSports.base.security.CustomOAuth2User;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FormLogin implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("유저 없음"));

//        return new CustomOAuth2User(member.getUsername(), member.getPassword(), member.getAuthorities());
        return new CustomOAuth2User(member.getUsername(), member.getAuthorities());
    }
}