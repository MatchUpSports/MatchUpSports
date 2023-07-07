package com.matchUpSports.base.initData;


import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.futsalField.service.FutsalFieldService;
import com.matchUpSports.boundedContext.match.entity.Match;
import com.matchUpSports.boundedContext.match.service.MatchService;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile({"dev", "test"})
public class NotProd {

    @Bean
    CommandLineRunner initData(
            FutsalFieldService fieldService, MemberService memberService, MatchService matchService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                Member m1 = memberService.join("asd@asd.com", "user1", 1).getData();
                Member m2 = memberService.join("asd@asd.com",  "user2", 2).getData();
                Member m3 = memberService.join("asd@asd.com",  "user3", 3).getData();
                Member m4 = memberService.join("asd@asd.com",  "user4", 4).getData();
                Member m5 = memberService.join("asd@asd.com",  "user5", 5).getData();
                Member m6 = memberService.join( "asd@asd.com",  "user6", 6).getData();
                Member m7 = memberService.join( "asd@asd.com",  "user7", 7).getData();
                Member m8 = memberService.join( "asd@asd.com",  "user8", 8).getData();
                Member m9 = memberService.join( "asd@asd.com",  "user9", 9).getData();
                Member kim = memberService.join("lantern50@tukorea.ac.kr", "KAKAO__2884083653", 1).getData();


                FutsalField f1 = fieldService.join("수원 풋살 경기장", "등록번호123", 100000, 10, "수원").getData();
                FutsalField f2 = fieldService.join("수원 풋살2", "등록번호123", 100000, 10, "수원").getData();
                FutsalField f3 = fieldService.join("수원 풋살3", "등록번호123", 100000, 10, "수원").getData();
                FutsalField f4 = fieldService.join("수원 풋살4", "등록번호123", 100000, 10, "수원").getData();

                Match match1 = matchService.join(1, f1, LocalDate.now()).getData();
                Match match2 = matchService.join(2, f1, LocalDate.now()).getData();
                Match match3 = matchService.join(3, f1, LocalDate.now()).getData();
                Match match4 = matchService.join(4, f1, LocalDate.now()).getData();

                matchService.addMatchMember(m1, match1);
                matchService.addMatchMember(m2, match1);
                matchService.addMatchMember(m3, match1);
                matchService.addMatchMember(m4, match1);
                matchService.addMatchMember(m5, match1);
                matchService.addMatchMember(m6, match1);
                matchService.addMatchMember(m7, match1);
                matchService.addMatchMember(m8, match1);
                matchService.addMatchMember(m9, match1);

            }
        };
    }
}
