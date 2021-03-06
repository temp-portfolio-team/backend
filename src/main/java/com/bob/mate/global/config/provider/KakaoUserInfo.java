package com.bob.mate.global.config.provider;

import com.bob.mate.domain.user.entity.Gender;

import java.util.Map;

public class KakaoUserInfo implements Oauth2UserInfo {


    private Map<String, Object> attributes;


    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return (String) getKakaoAccount().get("email");
    }

    @Override
    public String getNickName() {
        return (String) getProfile().get("nickname");
    }

    @Override
    public Gender getGender() {

        String gender = (String) getKakaoAccount().get("gender");

        if (gender != null) {
            if (gender.equals("male")) {
                return Gender.MAN;
            }
            return Gender.WOMAN;
        }
        return null;
    }

    @Override
    public String getImageUrl() {
        String imageUrl = (String) getProfile().get("profile_image_url");
        if (imageUrl == null) {
            imageUrl = "https://d3afymv2nzz1pw.cloudfront.net/doji.png";
        }
        return imageUrl;
    }

    public Map<String, Object> getKakaoAccount(){
        return(Map<String, Object>) attributes.get("kakao_account");
    }

    public Map<String, Object> getProfile(){
        return (Map<String, Object>) getKakaoAccount().get("profile");
    }
}
