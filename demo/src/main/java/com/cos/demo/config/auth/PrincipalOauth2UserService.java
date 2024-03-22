package com.cos.demo.config.auth;

import com.cos.demo.config.auth.provider.FacebookUserinfo;
import com.cos.demo.config.auth.provider.GoogleUserinfo;
import com.cos.demo.config.auth.provider.NaverUserinfo;
import com.cos.demo.config.auth.provider.OAuth2Userinfo;
import com.cos.demo.model.User;
import com.cos.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private CustomBCryptPasswordEncoder customBCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는
    // @AuthenticationPrincipal 어노테이션이 만들어진다
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration: "+userRequest.getClientRegistration()); // registrationId로 어떤 OAuth로 로그인됬는지 확인
        System.out.println("getAccessToken: "+userRequest.getAccessToken().getTokenValue());

        OAuth2User oauth2User = super.loadUser(userRequest);
        // 구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인 완료 -> code리턴(OAuth-Client라이브러리) -> Access Token요청
        // userRequest 정보 -> 회원프로필을 받아야함(loadUser함수) -> 구글로부터 회원프로필 받아준다
        System.out.println("getAttributes: "+oauth2User.getAttributes());

        // 회원가입 강제로 진행해볼예정
        OAuth2Userinfo oAuth2Userinfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2Userinfo = new GoogleUserinfo(oauth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            System.out.println("페이스북 로그인 요청");
            oAuth2Userinfo = new FacebookUserinfo(oauth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("네이버 로그인 요청");
            oAuth2Userinfo = new NaverUserinfo((Map)oauth2User.getAttributes().get("response"));
        }else{
            System.out.println("우리는 네이버와 구글과 페이스북만 지원해요");
        }

        String provider = oAuth2Userinfo.getProvider();
        String providerId = oAuth2Userinfo.getProviderId();
        String username = provider+"_"+providerId; //google_Id;
        String password = customBCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2Userinfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if(userEntity == null) {
            System.out.println("OAuth 로그인이 최초입니다");
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }else{
            System.out.println("로그인을 이미한적 있습니다. 당신은 자동회원가입이 되어있습니다");
        }

        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }
}
