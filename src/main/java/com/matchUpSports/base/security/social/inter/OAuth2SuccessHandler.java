package com.matchUpSports.base.security.social.inter;

import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final MemberService memberService;

    public OAuth2SuccessHandler(OAuth2AuthorizedClientService authorizedClientService, MemberService memberService) {
        this.authorizedClientService = authorizedClientService;
        this.memberService = memberService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        Member member = memberService.findByUsername(token.getPrincipal().getName());
        if (member != null) {
            if (member.getTier() == 0) {
                HttpSession session = request.getSession();
                session.setAttribute("member", member);
                setDefaultTargetUrl("/member/joiningForm");
            } else {
                setDefaultTargetUrl("/");
            }
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
