# toyproject-spring_security_DBAuthorization

Email: b635032_@daum.net

## 1. 개선 및 추가내용

## 2. 주요 로직 및 DB 연동 인가 핵심 아키텍처 

## 3. 트러블 슈팅

### 3.1 @OneToMany 양방향 매핑에서의 

로그인을 할 때, 아래와 같은 에러 메세지가 발생했다.

**failed to lazily initialize a collection of role: security.corespringsecurity.domain.entity.Account.accountRoleList, could not initialize proxy - no Session**

@OneToMany로 연관관계된 accountRoleList를 불러오지 못했다는 에러라고 생각했다.

Account와 AccountRole은 서로 1:N으로 양방향 매핑이 되어 있다. 하나의 Account는 여러 Role을 가질 수 있기 때문이다.

결론은 AuthenticationProvider에서 로그인 정보가 유효한지 확인하기 위해 AccountContext를 불러오는 loadUserByUsername 함수에 @Transactional을 붙여서 해결할 수 있었다.

(참고 링크: https://stackoverflow.com/questions/22821695/how-to-fix-hibernate-lazyinitializationexception-failed-to-lazily-initialize-a)

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

엔티티를 조회할 때, 영속성 컨텍스트의 1차 캐시에 엔티티가 존재하는지 먼저 확인한다. 만약 엔티티가 존재하지 않는다면 DB로부터 엔티티를 조회한다.

Account를 조회하는 시점에 영속성 컨텍스트가 생성되고 이를 통해 엔티티를 조회한다.
```java
userRepository.findByUsername(name)
```
그 후 영속성 컨텍스트가 종료되고 준영속 상태가  @OneToMany 지연로딩으로 등록된 AccountRoleList를 불러올 수 없었던 것이 문제였다.

이에 따라 @Transactional을 추가해 영속성 컨텍스트를 유지함으로써 문제를 해결할 수 있었다.

### 3.2 순환 참조 문제

Ajax 인증처리가 완료된 후, SuccessHandler의 objectMapper에서 로그인 성공한 객체를 직렬화(JSON으로 변환)해서 웹 페이지에 전달한다.

```java
objectMapper.writeValue(response.getWriter(), principal);
```

직렬화를 할 때, 엔티티 클래스의 toString 메소드를 이용하게 된다.

Lombok의 @ToString은 클래스의 모든 필드를 직렬화 한다. 그러므로, 양방향 연관관계가 매핑된 경우에 순환참조 문제가 발생하게 된다.

그러므로, 직접 toString 메소드를 생성하거나 @JsonIgnore을 이용해 순환 참조 문제를 피할 수 있다.

### 3.3 @Getter가 없을 때, Thymeleaf 템플릿에서 발생하는 문제
