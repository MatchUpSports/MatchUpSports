package com.matchUpSports.base.security.config;

import com.matchUpSports.base.security.social.inter.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.oauth2Login(oauth -> oauth
                        .loginPage("/member/login")
                        .successHandler(oAuth2SuccessHandler))
                .formLogin(form -> form.loginPage("/member/login"))
                .logout(logout -> logout
                        .invalidateHttpSession(true));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/adm/**").hasRole("ADMIN")
                .requestMatchers("/field/**").hasAnyRole("MANAGE", "ADMIN")
                .requestMatchers("/member/login").anonymous()
                .requestMatchers("/member/**").hasAnyRole("USER", "ADMIN", "MANAGE")
                .requestMatchers("/favicon.ico", "/resource/**", "/error",
                        "/image/**", "/js/**").permitAll()
                .anyRequest().authenticated());

        // "/match/**" 경로 추가
        http.csrf(c -> c.ignoringRequestMatchers("/product/more/**", "/match/**"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}