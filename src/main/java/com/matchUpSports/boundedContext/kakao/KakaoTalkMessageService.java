package com.matchUpSports.boundedContext.kakao;

import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;

@Service
public class KakaoTalkMessageService {      // 메시지를 전송하는 REST API 호출 서비스
    private static final String BASE_API = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private static final MediaType FORM_URL_ENCODED = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    // OkHttpClient를 사용하여 HTTP 요청을 보냄
    private final OkHttpClient client;

    public KakaoTalkMessageService() {
        this.client = new OkHttpClient();
    }

    // 액세스 토큰과 메시지를 받아서 Post 요청
    public void sendTextMessage(String accessToken, String text) throws IOException {
        String encodedText = URLEncoder.encode(text, "UTF-8");
        String encodedUrl = URLEncoder.encode("https://developers.kakao.com", "UTF-8");
        String encodedMobileUrl = URLEncoder.encode("https://developers.kakao.com", "UTF-8");

        String requestBodyString = "template_object={\"object_type\":\"text\",\"text\":\"" + encodedText +
                "\",\"link\":{\"web_url\":\"" + encodedUrl +
                "\",\"mobile_web_url\":\"" + encodedMobileUrl +
                "\"},\"button_title\":\"바로 확인\"}";

        System.out.println("카카오톡 requestBodyString: " + requestBodyString);

        RequestBody requestBody = RequestBody.create(FORM_URL_ENCODED, requestBodyString);

        // Authorization 헤더, Content-Type 헤더를 설정하고 Post 요청
        Request request = new Request.Builder()
                .url(BASE_API)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();

        // 응답이 성공적으로 도착했는지 확인하고 실패한 경우 오류 메시지 출력
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("카카오톡 메시지 전송 실패: " + response.body().string());
            }
        }
    }

}