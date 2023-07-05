package com.matchUpSports.boundedContext.member.service;

import com.matchUpSports.base.Role;
import com.matchUpSports.base.exception.handler.DataNotFoundException;
import com.matchUpSports.base.security.social.inter.DivideOAuth2User;
import com.matchUpSports.boundedContext.member.dto.*;
import com.matchUpSports.boundedContext.member.entity.Member;
import com.matchUpSports.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 유저입니다."));
    }

    @Transactional
    public Member saveOAuth2Member(DivideOAuth2User customOAuth2User) {
        Optional<Member> findByName = memberRepository.findByUsername(customOAuth2User.getUsername());

        if (findByName.isEmpty()) {
            Member member = Member.builder()
                    .username(customOAuth2User.getUsername())
                    .email(customOAuth2User.getEmail())
                    .build();
            member.addRole(Role.USER);

            return memberRepository.save(member);
        }

        return findByName.get();
    }

    @Transactional(readOnly = false)
    public Member createJoiningForm(JoiningForm joiningForm, Member member) {
        validateUserInput(joiningForm);

        Set<Role> memberAuthorities = new HashSet<>(Set.of(memberClassifier.get(joiningForm.getAuthorities())));
        Member memberWithUserInput = member.toBuilder()
                .email(joiningForm.getEmail())
                .phoneNumber(joiningForm.getPhone())
                .authorities(memberAuthorities)
                .bigDistrict(joiningForm.getBigDistrict())
                .smallDistrict(joiningForm.getSmallDistrict())
                .tier(tierClassifier.get(joiningForm.getTier()))
                .build();

        return memberRepository.save(memberWithUserInput);
    }

    private boolean validateUserInput(BasicUserInfoForm joiningForm) {
        String phoneNumberValidate = "^010[0-9]{7,8}$";
        if (!Pattern.matches(phoneNumberValidate, joiningForm.getPhone())) {
            throw new RuntimeException("잘못된 휴대전화번호 양식입니다");
        }

        if (!tiers.contains(joiningForm.getTier())) {
            throw new RuntimeException("잘못된 풋살 실력 양식입니다");
        }

        if (joiningForm.getBigDistrict() == null || joiningForm.getBigDistrict().equals("")) {
            throw new RuntimeException("잘못된 지역 양식입니다");
        }

        return true;
    }

    public ModifyingDisplaying showModifyingForm(long memberId) {
        Optional<Member> wrappedMember = memberRepository.findById(memberId);
        if (wrappedMember.isEmpty()) {
            return null;
        }
        Member member = wrappedMember.get();
        ModifyingDisplaying displayingForm = ModifyingDisplaying.builder()
                .email(member.getEmail())
                .phone(member.getPhoneNumber())
                .bigDistrict(member.getBigDistrict())
                .smallDistrict(member.getSmallDistrict())
                .tier(tierUnpacker.get(member.getTier()))
                .build();

        return displayingForm;
    }


    @Transactional(readOnly = false)
    public Member modify(ModifyingForm modifyingForm, Member member) {
        validateUserInput(modifyingForm);

        Member modifiedMember = member.toBuilder()
                .email(modifyingForm.getEmail())
                .phoneNumber(modifyingForm.getPhone())
                .bigDistrict(modifyingForm.getBigDistrict())
                .smallDistrict(modifyingForm.getSmallDistrict())
                .tier(tierClassifier.get(modifyingForm.getTier()))
                .build();
        return memberRepository.save(modifiedMember);
    }

    public MyPage showMyPage(Member member) {
        return MyPage.builder()
                .email(member.getEmail())
                .phone(member.getPhoneNumber())
                .bigDistrict(member.getBigDistrict())
                .smallDistrict(member.getSmallDistrict())
                .tier(tierUnpacker.get(member.getTier()))
                .createdDate(member.getCreatedDate().toLocalDate())
                .username(member.getUsername())
                .winningRate(convertWinningRateToString(member.getWinningRate()))
                .authorities(convertAuthoritiesToString(member.getAuthorities()))
                .build();
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
}
