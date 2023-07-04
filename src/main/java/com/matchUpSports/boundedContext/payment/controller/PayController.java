package com.matchUpSports.boundedContext.payment.controller;


import com.matchUpSports.base.rq.Rq;
import com.matchUpSports.boundedContext.match.service.MatchService;
import com.matchUpSports.boundedContext.member.service.MemberService;
import com.matchUpSports.boundedContext.payment.service.PayService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Controller
@RequiredArgsConstructor
@RequestMapping(value="/pay")
public class PayController {
    private final MemberService memberService;
    private final MatchService matchService;
    private final PayService payService;
    private final Rq rq;

    @GetMapping("/{matchId}")
    public String pay(Model model, @PathVariable Long matchId){

        //model.addAttribute("member", memberService.memberFindById(memberId));
        //model.addAttribute("match", matchService.matchFindById(matchId));
        return "payment/payment";
    }

    @GetMapping("/success")
    public String paymentResult(
            Model model,
            @RequestParam(value = "orderId") String orderId,
            @RequestParam(value = "amount") Integer amount,
            @RequestParam(value = "paymentKey") String paymentKey) throws Exception {

        String secretKey = "test_sk_jkYG57Eba3GwB6zqY2z3pWDOxmA1:";

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(secretKey.getBytes("UTF-8"));
        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

        URL url = new URL("https://api.tosspayments.com/v1/payments/" + paymentKey);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200 ? true : false;
        model.addAttribute("isSuccess", isSuccess);

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();

        // 카카오톡 메시지로 해당 멤버들에게 채팅 메시지 보내기 적어야함

        model.addAttribute("responseStr", jsonObject.toJSONString());
        System.out.println(jsonObject.toJSONString());

        model.addAttribute("method",  jsonObject.get("method"));
        model.addAttribute("orderName", jsonObject.get("orderName"));

        if ((jsonObject.get("method")) != null) {
            if ((jsonObject.get("method")).equals("카드")) {
                model.addAttribute("cardNumber",  ((JSONObject) jsonObject.get("card")).get("number"));
            } else if ((jsonObject.get("method")).equals("가상계좌")) {
                model.addAttribute("accountNumber", ((JSONObject) jsonObject.get("virtualAccount")).get("accountNumber"));
            } else if ((jsonObject.get("method")).equals("계좌이체")) {
                model.addAttribute("bank", ((JSONObject) jsonObject.get("transfer")).get("bank"));
            } else if ((jsonObject.get("method")).equals("휴대폰")) {
                model.addAttribute("customerMobilePhone",  ((JSONObject) jsonObject.get("mobilePhone")).get("customerMobilePhone"));
            }
        } else {
            model.addAttribute("code", jsonObject.get("code"));
            model.addAttribute("message", jsonObject.get("message"));
        }

        return rq.redirectWithMsg("/vote/" + "매치아이디", "시설 이용료를 지불하였습니다.");
    }

    @GetMapping("/fail")
    public String paymentResult(
            Model model,
            @RequestParam(value = "message") String message,
            @RequestParam(value = "code") Integer code
    ){
        model.addAttribute("code", code);
        model.addAttribute("message", message);
        return "payment/fail";
    }
}
