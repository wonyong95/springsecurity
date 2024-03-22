package com.cos.demo.config.auth.provider;

import java.util.Map;

public class FacebookUserinfo implements OAuth2Userinfo{

    private Map<String,Object> attributes; // oauth2User.getAttributes()

    public FacebookUserinfo(Map<String,Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "facebook";
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
