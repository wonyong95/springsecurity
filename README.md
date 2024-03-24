``` java
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize,postAuthorize 어노테이션 활성화
// EnableMethodSecurity 글로벌 붙은거 사라짐

@Secured("ROLE_ADMIN")
@GetMapping("/info")
public @ResponseBody String info(){
    return "개인정보";
}

@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
@GetMapping("/data")
public @ResponseBody String data(){
    return "데이터정보";
}
```


---

## 구글 콘솔 api 

oAuth: open Auth

리소스 오너
클라이언트
인증 서버
리소스 서버

https://console.cloud.google.com/

사용자 인증정보 -> 만들기


```html

<a href="/oauth2/authorization/google">구글 로그인</a>

/oauth2/authorization/google 마음대로 바꿀수없음

```


```java

.oauth2Login(login ->
    login
    .loginPage("/loginForm") // 구글 로그인이 완료된 뒤의 후처리가 필요함
    .defaultSuccessUrl("/" , true)
    );

```

### 구글 로그인 후처리

```java

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest: "+userRequest.getClientRegistration());
        System.out.println("userRequest: "+userRequest.getAccessToken().getTokenValue());
        System.out.println("userRequest: "+super.loadUser(userRequest).getAttributes());
        return super.loadUser(userRequest);
    }
}
```

데이터 정보 ->
sub=100325407570986432096, 
name=윤원용, 
given_name=원용, 
family_name=윤, 
picture=https://lh3.googleusercontent.com/a/ACg8ocJXEu-jiyOVHobij5qdclFfv9krmQcVhXl9tqA0n6si=s96-c, 
email=2dawit70@gmail.com, 
email_verified=true, 
locale=ko


username = "google_100325407570986432096"
password = "암호화(겟인데어)"
email = "2dawit70@gmail.com"
role = "ROLE_USER"
provider = "google"
providerId = "100325407570986432096"

### Authentication 객체의 두가지 타입

![img.png](img.png)


![img_1.png](img_1.png)

UserDetails, OAuth2User -> PrincipalDetails를 부모로두고 Authentication에 넣어서쓰면됨

```java
    public @ResponseBody String testLogin(
            Authentication authentication,
            @AuthenticationPrincipal PrincipalDetails userDetails){ // DI(의존성 주입)
        System.out.println("/test/login =========");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication: "+principalDetails.getUser());

        System.out.println("userDetails: "+userDetails.getUsername());
        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oauth){ // DI(의존성 주입)
        System.out.println("/test/oauth/login =========");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication: "+oAuth2User.getAttributes());
        System.out.println("oauth2User:"+oauth.getAttributes());

        return "OAuth 세션 정보 확인하기";
    }
```

### 구글 로그인 및 자동회원가입 

BCryptPasswordEncoder 의존성 문제로 CustomBCryptPasswordEncoder 클래스 생성함
```java
@Component // @Component 스캔하기위해 추가
public class CustomBCryptPasswordEncoder extends BCryptPasswordEncoder {
}

```

- user -> userinfo 로변경 
- 이유: 겟매핑전에 프린트문이 출력됨
```java
    @GetMapping("/userinfo")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails: "+principalDetails.getUser());
        return "user";
    }
```

### JWT 

1. JWT 
   - JSON WEB TOKEN
  
2. session
   - 최초 Request 요청시 Response .html의 Header 쿠기에 SessionID를 부여받음 이후 요청시 header에 SessionID 추가하여 요청

- sessionID 삭제 방법
  -  서버쪽에서 session 삭제 (강제)
  - 사용자 브라우저 종료
  - 특정 시간 이후 서버 sessionID 만료 (평균 30분)
- 단점
  - 동접자 수가 많으면 서버에 부하가 걸림 -> 로드 밸런싱 (애플리케이션을 지원하는 리소스 풀 전체에 네트워크 트래픽을 균등하게 배포하는 방법)
  - 해결하기 위해 JWT를 사용함

3. TCP

- 통신: OSI 7계층
- 응용
  - 사진(100)
- 프리젠테이션
  - 암호화, 압축
- 세션계층
  - 인층 체크
- 트랜스포트
  - TCP/UDP
  - TCP: 웹
  - UDP: 사람이 이해할수있는것(전화)
- 네트워크
  - IP
- 데이터링크
  - LAN, WAN
- 물리
  - 연결선

4. CIA
- 기밀성 무결성(변경) 가용성
- 문서를 암호화해서 전달해 주어야 함
  - 열쇠 전달 문제
  - 문서 누구로부터 왔는가