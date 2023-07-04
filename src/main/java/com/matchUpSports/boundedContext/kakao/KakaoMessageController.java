package com.matchUpSports.boundedContext.kakao;

import com.matchUpSports.base.rq.Rq;
import com.matchUpSports.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class KakaoMessageController {

    private final KakaoTalkMessageService kakaoTalkMessageService;
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/send")
    public String sendMessage(@AuthenticationPrincipal User user) throws IOException {
        // 멤버의 액세스 토큰을 저장
        String tokenValue = memberService.getAccessToken(user.getUsername());

        if (tokenValue == null) {
            return rq.redirectWithMsg("/kakao/login","카카오 로그인 필요");
        }

        String message = "이거 설마 된건가...? 제발!!!!!!!!";

        // 액세스 토큰과 메시지로 REST API 요청하는 메서드
        kakaoTalkMessageService.sendTextMessage(tokenValue, message);

        return rq.redirectWithMsg("/", "메시지 전송 완료");
    }
}
