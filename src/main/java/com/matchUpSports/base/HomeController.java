package com.matchUpSports.base;

import com.matchUpSports.base.security.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {


    @GetMapping("/")
    public String every() {
        return "member/home.html";
    }

    @GetMapping("/member/login")
    public String login() {
        return "member/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        new SecurityContextLogoutHandler()
                .logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return "redirect:/";
    }

    @ResponseBody
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/member")
    public CustomOAuth2User member(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {

        return customOAuth2User;
    }

    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "ADMIN";
    }

    @ResponseBody
    @PreAuthorize("hasRole('MANAGE')")
    @GetMapping("/management")
    public String management() {
        return "MANAGE";
    }
}