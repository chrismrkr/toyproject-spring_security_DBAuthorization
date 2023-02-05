# toyproject-spring_security_DBAuthorization

Email: b635032_@daum.net

## 1. 개선 및 추가내용

## 2. 주요 로직 및 DB 연동 인가 핵심 아키텍처 

## 3. 트러블 슈팅

### 3.1 @OneToMany 양방향 매핑에서의 이슈

로그인을 할 때, 아래와 같은 에러 메세지가 발생했다.

**failed to lazily initialize a collection of role: security.corespringsecurity.domain.entity.Account.accountRoleList, could not initialize proxy - no Session**

@OneToMany로 연관관계된 accountRoleList를 불러오지 못했다는 에러라고 생각했다.

Account와 AccountRole은 서로 1:N으로 양방향 매핑이 되어 있다. 하나의 Account는 여러 Role을 가질 수 있기 때문이다.

결론은 AuthenticationProvider에서 로그인 정보가 유효한지 확인하기 위해 AccountContext를 불러오는 loadUserByUsername 함수에 @Transactional을 붙여서 해결할 수 있었다.

```java
/* AuthenticationProvider */
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String)authentication.getCredentials();

        AccountContext accountContext = (AccountContext)userDetailsService.loadUserByUsername(username);

        if(!passwordEncoder.matches(password, accountContext.getAccount().getPassword())) {
           throw new BadCredentialsException("BadCredentialException");
        } 
        ...
    }
}
```

```java
@Service("UserDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional /* 이것을 추가하여 문제가 해결됨 */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ...
        return accountContext;
    }
}
```


