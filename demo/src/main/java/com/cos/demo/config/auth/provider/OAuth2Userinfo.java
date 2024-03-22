package com.cos.demo.config.auth.provider;

public interface OAuth2Userinfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}
