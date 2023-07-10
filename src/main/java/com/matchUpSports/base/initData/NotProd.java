package com.matchUpSports.base.initData;


import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
import com.matchUpSports.boundedContext.futsalField.repository.FutsalFieldRepository;
import com.matchUpSports.boundedContext.futsalField.service.FutsalFieldService;
import com.matchUpSports.boundedContext.match.Form.MatchForm;
import com.matchUpSports.boundedContext.match.service.MatchService;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Configuration
@Profile({"dev", "test"})
public class NotProd {

    @Bean
    CommandLineRunner initData(
            FutsalFieldService fieldService, MemberService memberService, MatchService matchService, FutsalFieldRepository futsalFieldRepository
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                Member m1 = memberService.join("asd@asd.com", "user1", 1).getData();
                Member m2 = memberService.join("asd@asd.com", "user2", 2).getData();
                Member m3 = memberService.join("asd@asd.com", "user3", 3).getData();
                Member m4 = memberService.join("asd@asd.com", "user4", 4).getData();
                Member m5 = memberService.join("asd@asd.com", "user5", 5).getData();
                Member m6 = memberService.join("asd@asd.com", "user6", 6).getData();
                Member m7 = memberService.join("asd@asd.com", "user7", 7).getData();
                Member m8 = memberService.join("asd@asd.com", "user8", 8).getData();
                Member m9 = memberService.join("asd@asd.com", "user9", 9).getData();
                Member fieldUser = memberService.join("suwon@naver.com", "수원 풋살 시설 관리자", 0).getData();


                FutsalField f1 = fieldService.join("수원 풋살 경기장", "등록번호123", 100000, 10, "수원", fieldUser).getData();
                FutsalField f2 = fieldService.join("수원 풋살2", "등록번호123", 100000, 10, "수원", fieldUser).getData();
                FutsalField f3 = fieldService.join("수원 풋살3", "등록번호123", 100000, 10, "수원", fieldUser).getData();
                FutsalField f4 = fieldService.join("수원 풋살4", "등록번호123", 100000, 10, "수원", fieldUser).getData();

                MatchForm matchForm1 = new MatchForm(LocalDate.now(), "수원", "수원 풋살 경기장", 1, m1.getId(), m1.getEmail(), f1);
                MatchForm matchForm2 = new MatchForm(LocalDate.now(), "수원", "수원 풋살 경기장", 1, m2.getId(), m2.getEmail(), f1);
                MatchForm matchForm3 = new MatchForm(LocalDate.now(), "수원", "수원 풋살 경기장", 1, m3.getId(), m3.getEmail(), f1);
                MatchForm matchForm4 = new MatchForm(LocalDate.now(), "수원", "수원 풋살 경기장", 1, m4.getId(), m4.getEmail(), f1);
                MatchForm matchForm5 = new MatchForm(LocalDate.now(), "수원", "수원 풋살 경기장", 1, m5.getId(), m5.getEmail(), f1);
                MatchForm matchForm6 = new MatchForm(LocalDate.now(), "수원", "수원 풋살 경기장", 1, m6.getId(), m6.getEmail(), f1);
                MatchForm matchForm7 = new MatchForm(LocalDate.now(), "수원", "수원 풋살 경기장", 1, m7.getId(), m7.getEmail(), f1);
                MatchForm matchForm8 = new MatchForm(LocalDate.now(), "수원", "수원 풋살 경기장", 1, m8.getId(), m8.getEmail(), f1);
                MatchForm matchForm9 = new MatchForm(LocalDate.now(), "수원", "수원 풋살 경기장", 1, m9.getId(), m9.getEmail(), f1);

                matchService.createMatch(matchForm1, m1.getId());
                matchService.createMatch(matchForm2, m2.getId());
                matchService.createMatch(matchForm3, m3.getId());
                matchService.createMatch(matchForm4, m4.getId());
                matchService.createMatch(matchForm5, m5.getId());
                matchService.createMatch(matchForm6, m6.getId());
                matchService.createMatch(matchForm7, m7.getId());
                matchService.createMatch(matchForm8, m8.getId());
                matchService.createMatch(matchForm9, m9.getId());

            }
        };
    }
}
