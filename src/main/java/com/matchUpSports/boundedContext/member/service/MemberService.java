package com.matchUpSports.boundedContext.member.service;

import com.matchUpSports.base.Role;
import com.matchUpSports.base.districts.Districts;
import com.matchUpSports.base.exception.handler.DataNotFoundException;
import com.matchUpSports.base.rsData.RsData;
import com.matchUpSports.base.security.social.inter.DivideOAuth2User;
import com.matchUpSports.boundedContext.member.dto.*;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private static final Map<String, Integer> tierClassifier = new HashMap<>(Map.of("하수", 1, "중수", 2, "고수", 3));
    private static final Map<Integer, String> tierUnpacker = new HashMap<>(Map.of(1, "하수", 2, "중수", 3, "고수"));
    private static final Map<String, Role> memberClassifier = new HashMap<>(Map.of("일반 유저", Role.USER, "시설 주인", Role.MANAGE, "관리자", Role.ADMIN));
    private static final List<String> tiers = new ArrayList<>(Arrays.asList("하수", "중수", "고수"));
    @Autowired
    private Districts dstricts;

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 유저입니다."));
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional
    public Member saveOAuth2Member(DivideOAuth2User customOAuth2User, String accessToken, String nickname) {
        Optional<Member> findByName = memberRepository.findByUsername(customOAuth2User.getUsername());

        if (findByName.isEmpty()) {
            Member member = Member.builder()
                    .username(customOAuth2User.getUsername())
                    .nickname(nickname)
                    .email(customOAuth2User.getEmail())
                    .accessToken(accessToken)
                    .accessTokenTime(LocalDateTime.now())
                    .build();
            member.addRole(Role.USER);

            return memberRepository.save(member);
        }

        return findByName.get();
    }

    @Transactional(readOnly = false)
    public RsData<Member> createJoiningForm(JoiningForm joiningForm, Member member) {
        RsData<Member> isValidated = validateUserInput(joiningForm);
        if(isValidated.isFail()) {
            return isValidated;
        }

        Set<Role> memberAuthorities = new HashSet<>(Set.of(memberClassifier.get(joiningForm.getAuthorities())));
        Member memberWithUserInput = member.toBuilder()
                .nickname(joiningForm.getNickname())
                .email(joiningForm.getEmail())
                .phoneNumber(joiningForm.getPhone())
                .authorities(memberAuthorities)
                .bigDistrict(joiningForm.getBigDistrict())
                .smallDistrict(joiningForm.getSmallDistrict())
                .tier(tierClassifier.get(joiningForm.getTier()))
                .build();

        return RsData.of("S-1", "최조 사용자 정보 기입이 완료되었습니다", memberRepository.save(memberWithUserInput));
    }

    private RsData<Member> validateUserInput(BasicUserInfoForm joiningForm) {
        String phoneNumberValidate = "^010[0-9]{7,8}$";
        if (!Pattern.matches(phoneNumberValidate, joiningForm.getPhone())) {
            return RsData.of("F-1", "잘못된 휴대전화번호 양식입니다");
        }

        if (!tiers.contains(joiningForm.getTier())) {
            return RsData.of("F-2", "잘못된 풋살 실력 양식입니다");
        }

        if (joiningForm.getBigDistrict() == null || joiningForm.getBigDistrict().equals("")) {
            return RsData.of("F-3", "잘못된 지역 양식입니다");
        }

        if (!dstricts.getBigDistricts().contains(joiningForm.getBigDistrict())
                || !dstricts.getSmallDistricts().get(joiningForm.getBigDistrict()).contains(joiningForm.getSmallDistrict())) {
            return RsData.of("F-4", "잘못된 지역 양식입니다");
        }

        return RsData.of("S-1", "사용자 입력 데이터 검증 완료");
    }

    public RsData<ModifyingDisplaying> showModifyingForm(long memberId) {
        Optional<Member> wrappedMember = memberRepository.findById(memberId);
        if (wrappedMember.isEmpty()) {
            return RsData.of("F-5", "존재하지 않는 사용자입니다");
        }
        Member member = wrappedMember.get();
        ModifyingDisplaying displayingForm = ModifyingDisplaying.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .phone(member.getPhoneNumber())
                .bigDistrict(member.getBigDistrict())
                .smallDistrict(member.getSmallDistrict())
                .tier(tierUnpacker.get(member.getTier()))
                .build();

        return RsData.of("S-1", "수정 페이지 불러옴", displayingForm);
    }


    @Transactional(readOnly = false)
    public RsData<Member> modify(ModifyingForm modifyingForm, Member member) {
        RsData<Member> isValidated = validateUserInput(modifyingForm);
        if(isValidated.isFail()) {
            return isValidated;
        }

        Member modifiedMember = member.toBuilder()
                .nickname(modifyingForm.getNickname())
                .email(modifyingForm.getEmail())
                .phoneNumber(modifyingForm.getPhone())
                .bigDistrict(modifyingForm.getBigDistrict())
                .smallDistrict(modifyingForm.getSmallDistrict())
                .tier(tierClassifier.get(modifyingForm.getTier()))
                .build();
        return RsData.of("S-1", "사용자 정보 수정 성공", memberRepository.save(modifiedMember));
    }

    public MyPage showMyPage(Member member) {
        int win = member.getWin();
        int lose = member.getLose();
        return MyPage.builder()
                .email(member.getEmail())
                .phone(member.getPhoneNumber())
                .bigDistrict(member.getBigDistrict())
                .smallDistrict(member.getSmallDistrict())
                .tier(tierUnpacker.get(member.getTier()))
                .createdDate(member.getCreatedDate().toLocalDate())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .winningRate(calculateWinningRate(win, lose))
                .authorities(convertAuthoritiesToString(member.getAuthorities()))
                .build();
    }

    @Transactional
    public String getAccessToken(String userName) {
        return memberRepository.findByUsername(userName).get().getAccessToken();
    }

    // 액세스 토큰이 만료됐는지 확인하는 메서드
    public boolean checkAccessTokenIsExpired(String userName) {
        LocalDateTime myTokenTime = memberRepository.findByUsername(userName).get().getAccessTokenTime();

        if (myTokenTime.equals(null)) {
            return true;
        }

        return myTokenTime.plusHours(12).isBefore(LocalDateTime.now());
    }

    private String calculateWinningRate(int win, int lose) {
        float winningRate = 0f;
        float under = win + lose;
        if (under != 0) {
            winningRate = (Math.round(win / (under)) * 100) / 100.0f;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(winningRate);
        stringBuffer.append("%");
        return stringBuffer.toString();
    }

    private String convertWinningRateToString(int winningRate) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(winningRate);
        stringBuffer.append("%");
        return stringBuffer.toString();
    }

    private List<String> convertAuthoritiesToString(List<? extends GrantedAuthority> simpleGrantedAuthorities) {
        return simpleGrantedAuthorities.stream()
                .map(simpleGrantedAuthority -> simpleGrantedAuthority.getAuthority().substring(5
                        , simpleGrantedAuthority.getAuthority().length()))
                .toList();
    }

    @Transactional
    public void deleteHard(Long id) {
        memberRepository.deleteHardById(id);
    }

    @Transactional
    public RsData<Member> join(String email, String userName, String nickName, int tier) {
        Member member = Member.builder()
                .email(email)
                .username(userName)
                .nickname(nickName)
                .tier(tier)
                .build();
        memberRepository.save(member);

        return RsData.of("S-1", "회원가입 완료", member);
    }
}
