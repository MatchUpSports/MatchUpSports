package com.matchUpSports.base.security.social;

import com.matchUpSports.base.exception.NotSupportUserLoginException;
import com.matchUpSports.base.security.social.inter.DivideOAuth2User;
import com.matchUpSports.base.security.social.user.KakaoUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static com.matchUpSports.base.security.social.OAuth2Provider.KAKAO;

public class SocialUserFactory {

    public static DivideOAuth2User create(String providerTypeCode, OAuth2User oAuth2User) {

        if (isMatchWithProvider(providerTypeCode, KAKAO)) {
            return new KakaoUser(oAuth2User);
        }

        throw new NotSupportUserLoginException("지원하지 않는 로그인 방식입니다.");
    }

    private static boolean isMatchWithProvider(String providerTypeCode, OAuth2Provider provider) {
        return providerTypeCode.equals(provider.name());
    }

}

