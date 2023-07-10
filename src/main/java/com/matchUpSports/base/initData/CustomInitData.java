//package com.matchUpSports.base.initData;
//
//import com.matchUpSports.boundedContext.futsalField.repository.FutsalFieldRepository;
//import com.matchUpSports.boundedContext.futsalField.service.FutsalFieldService;
//import com.matchUpSports.boundedContext.member.repository.MemberRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.transaction.annotation.Transactional;
//
//
//@Profile({"dev", "test", "prod"})
//@Configuration
//public class CustomInitData {
//    @Bean
//    CommandLineRunner initData(MemberRepository memberRepository, FutsalFieldRepository fieldRepository, FutsalFieldService futsalFieldService) {
//
//        return new CommandLineRunner() {
//
//            @Override
//            @Transactional
//            public void run(String... args) throws Exception {
////
////                Member user = Member.builder()
////                        .username("user1")
////                        .phoneNumber("010-1111-1111")
////                        .email("user1@naver.com")
////                        .build();
////                user.addRole(Role.MANAGE);
////                memberRepository.save(user);
//
////                FutsalField futsalField = FutsalField.builder()
////                        .member(user)
////                        .fieldName("아디다스 더 베이스 서울")
////                        .fieldLocation("서울 용산구")
////                        .price(100000)
////                        .openTime(LocalTime.of(9, 0))
////                        .closeTime(LocalTime.of(22, 0))
////                        .courtCount(10)
////                        .registNum("A")
////                        .build();
////                fieldRepository.save(futsalField);
//
//            }
//        };
//    }
//}