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

oAuth: open Auth

리소스 오너
클라이언트
인증 서버
리소스 서버

https://console.cloud.google.com/

사용자 인증정보 만들기 -> 