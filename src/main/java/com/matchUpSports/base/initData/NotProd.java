package com.matchUpSports.base.initData;


import com.matchUpSports.boundedContext.futsalField.entity.FutsalField;
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
            FutsalFieldService fieldService, MemberService memberService, MatchService matchService
    ) {
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                Member m1 = memberService.join("asd@asd.com", "user1", "user1", 1).getData();
                Member m2 = memberService.join("asd@asd.com", "user2", "user2", 2).getData();
                Member m3 = memberService.join("asd@asd.com", "user3", "user3", 3).getData();
                Member m4 = memberService.join("asd@asd.com", "user4", "user4", 4).getData();
                Member m5 = memberService.join("asd@asd.com", "user5", "user5", 5).getData();
                Member m6 = memberService.join("asd@asd.com", "user6", "user6", 6).getData();
                Member m7 = memberService.join("asd@asd.com", "user7", "user7", 7).getData();
                Member m8 = memberService.join("asd@asd.com", "user8", "user8", 8).getData();
                Member m9 = memberService.join("asd@asd.com", "user9", "user9", 9).getData();
                Member m10 = memberService.join("asd@asd.com", "user10", "user10", 1).getData();
                Member fieldUser = memberService.join("seoul@naver.com", "서울 풋살 시설 관리자", "서울 풋살 시설 관리자", 0).getData();


                FutsalField f1 = fieldService.join("서울 풋살 경기장", "등록번호123", 100000, 10, "서울", fieldUser).getData();
                FutsalField f2 = fieldService.join("서울 풋살2", "등록번호123", 100000, 10, "서울", fieldUser).getData();
                FutsalField f3 = fieldService.join("서울 풋살3", "등록번호123", 100000, 10, "서울", fieldUser).getData();
                FutsalField f4 = fieldService.join("서울 풋살4", "등록번호123", 100000, 10, "서울", fieldUser).getData();

                //하루 추가된날짜 당일 예약 금지떄문에
                LocalDate tomorrow = LocalDate.now().plusDays(1);

                MatchForm matchForm1 = new MatchForm(tomorrow, "서울", "서울 풋살 경기장", 1, m1.getId(), m1.getEmail(), f1);
                MatchForm matchForm2 = new MatchForm(tomorrow, "서울", "서울 풋살 경기장", 1, m2.getId(), m2.getEmail(), f1);
                MatchForm matchForm3 = new MatchForm(tomorrow, "서울", "서울 풋살 경기장", 1, m3.getId(), m3.getEmail(), f1);
                MatchForm matchForm4 = new MatchForm(tomorrow, "서울", "서울 풋살 경기장", 1, m4.getId(), m4.getEmail(), f1);
                MatchForm matchForm5 = new MatchForm(tomorrow, "서울", "서울 풋살 경기장", 1, m5.getId(), m5.getEmail(), f1);
                MatchForm matchForm6 = new MatchForm(tomorrow, "서울", "서울 풋살 경기장", 1, m6.getId(), m6.getEmail(), f1);
                MatchForm matchForm7 = new MatchForm(tomorrow, "서울", "서울 풋살 경기장", 1, m7.getId(), m7.getEmail(), f1);
                MatchForm matchForm8 = new MatchForm(tomorrow, "서울", "서울 풋살 경기장", 1, m8.getId(), m8.getEmail(), f1);
                MatchForm matchForm9 = new MatchForm(tomorrow, "서울", "서울 풋살 경기장", 1, m9.getId(), m9.getEmail(), f1);
                MatchForm matchForm10 = new MatchForm(tomorrow, "서울", "서울 풋살 경기장", 1, m10.getId(), m10.getEmail(), f1);

                MatchForm matchForm11 = new MatchForm(tomorrow, "서울", "서울 풋살2", 1, m1.getId(), m1.getEmail(), f2);
                MatchForm matchForm12 = new MatchForm(tomorrow, "서울", "서울 풋살2", 1, m2.getId(), m2.getEmail(), f2);
                MatchForm matchForm13 = new MatchForm(tomorrow, "서울", "서울 풋살2", 1, m3.getId(), m3.getEmail(), f2);
                MatchForm matchForm14 = new MatchForm(tomorrow, "서울", "서울 풋살2", 1, m4.getId(), m4.getEmail(), f2);
                MatchForm matchForm15 = new MatchForm(tomorrow, "서울", "서울 풋살2", 1, m5.getId(), m5.getEmail(), f2);
                MatchForm matchForm16 = new MatchForm(tomorrow, "서울", "서울 풋살2", 1, m6.getId(), m6.getEmail(), f2);
                MatchForm matchForm17 = new MatchForm(tomorrow, "서울", "서울 풋살2", 1, m7.getId(), m7.getEmail(), f2);
                MatchForm matchForm18 = new MatchForm(tomorrow, "서울", "서울 풋살2", 1, m8.getId(), m8.getEmail(), f2);
                MatchForm matchForm19 = new MatchForm(tomorrow, "서울", "서울 풋살2", 1, m9.getId(), m9.getEmail(), f2);

                matchService.createMatch(matchForm1, m1.getId());
                matchService.createMatch(matchForm2, m2.getId());
                matchService.createMatch(matchForm3, m3.getId());
                matchService.createMatch(matchForm4, m4.getId());
                matchService.createMatch(matchForm5, m5.getId());
                matchService.createMatch(matchForm6, m6.getId());
                matchService.createMatch(matchForm7, m7.getId());
                matchService.createMatch(matchForm8, m8.getId());
                matchService.createMatch(matchForm9, m9.getId());
                matchService.createMatch(matchForm10, m10.getId());

                matchService.createMatch(matchForm11, m1.getId());
                matchService.createMatch(matchForm12, m2.getId());
                matchService.createMatch(matchForm13, m3.getId());
                matchService.createMatch(matchForm14, m4.getId());
                matchService.createMatch(matchForm15, m5.getId());
                matchService.createMatch(matchForm16, m6.getId());
                matchService.createMatch(matchForm17, m7.getId());
                matchService.createMatch(matchForm18, m8.getId());
                matchService.createMatch(matchForm19, m9.getId());

            }
        };
    }
}