package com.matchUpSports.base.security;

import com.matchUpSports.base.security.social.SocialUserFactory;
import com.matchUpSports.base.security.social.inter.DivideOAuth2User;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2Login extends DefaultOAuth2UserService {

    private final MemberService memberService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        String providerName = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 카카오 로그인 액세스 토큰
        String accessToken = userRequest.getAccessToken().getTokenValue().toString();

        DivideOAuth2User customOAuth2User = SocialUserFactory.create(providerName, oAuth2User);

        Member member = memberService.saveOAuth2Member(customOAuth2User, accessToken, "annonymous");

        return new CustomOAuth2User(member.getUsername(), "", member.getAuthorities());
    }

}