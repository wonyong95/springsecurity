package com.cos.demo.config.auth.provider;

import java.util.Map;

public class NaverUserinfo implements OAuth2Userinfo{

    private Map<String,Object> attributes; // oauth2User.getAttributes()

    // {id=faYbBNZG5pEdgQFTG7yBnIXqsON7LmIYg45hrWyVnto, email=2dawit12@naver.com, name=윤원용}
    public NaverUserinfo(Map<String,Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
