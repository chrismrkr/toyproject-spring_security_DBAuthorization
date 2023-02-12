# toyproject-spring_security_DBAuthorization

Email: b635032_@daum.net

## 1. 개선 및 추가내용

**Java 8, Spring Boot 2.7, JPA, Spring Security, Thymeleaf Template, lombok**

+ DB 연동 인가 기능 추가

## 2. 주요 로직 및 DB 연동 인가 핵심 아키텍처 

### 2.1 인가 처리 아키텍처

![dbauth_flow](https://user-images.githubusercontent.com/62477958/218303215-6dce0f67-e5b4-4d24-8cc3-fca7886c7f3f.png)

새로운 FilterSecurityInterceptor 클래스를 생성해서 아래의 코드를 통해 등록했다.

```java
   http.addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
```

### 2.2 추가된 비즈니스 로직

권한 생성, 수정, 삭제를 위한 Controller, Service, Repository를 추가했다.

권한을 통해 접근할 수 있는 자원을 생성, 수정, 삭제하기 위한 Controller, Service, Repository를 추가했다.

## 3. 트러블 슈팅

개발하면서 생긴 문제를 해결한 과정을 기술한다.

### 3.1 @OneToMany 양방향 매핑에서의 이슈

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

JPA의 영속, 준영속, 비영속 상태를 영속성 컨텍스트에 연결지어 확실히 이해해야 한다.

### 3.2 순환 참조 문제

Ajax 인증처리가 완료된 후, SuccessHandler의 objectMapper에서 로그인 성공한 객체를 직렬화(JSON으로 변환)해서 웹 페이지에 전달한다.

```java
objectMapper.writeValue(response.getWriter(), principal);
```

직렬화를 할 때, 엔티티 클래스의 toString 메소드를 이용하게 된다.

Lombok의 @ToString은 클래스의 모든 필드를 직렬화 한다. 이에 양방향 연관관계가 매핑된 경우에 순환참조 문제가 발생하게 된다.

직접 toString 메소드를 생성하거나 @JsonIgnore을 이용해 순환 참조 문제를 피할 수 있었다.

### 3.3 Thymeleaf 템플릿의 Getter 활용

Thymeleaf 템플릿에서는 Model을 통해 전달한 객체 리스트를 동적으로 바인딩하는 방식이 있다.

```html
            <tbody>
            <tr th:each="role : ${roles}">
                <td><a th:href="@{|/admin/roles/${role.id}|}" th:text="${role.roleName}"></a></td>
                <td th:text="${role.roleDescription}"></td>
            </tr>
            </tbody>
```

Getter가 없으면 동작하지 않는다.


### 3.4 스프링에서 파라미터를 받기 위해 사용하는 Annotation 정리

HTTP POST 요청에 대한 응답 데이터는 MessageBody에 담겨서 온다.

content-type이 application/x-www-form-urlencoded라면 parameter=value&another=value와 같은 모양으로 messageBody에 저장된다.

반면, content-type이 application/json이라면, { parmeter: value, another: value }와 같은 모양으로 messageBody에 저장되어 전송된다.

그러므로, @RequestParam, @RequestBody 모두 messageBody를 이용하는 것이므로 혼동하지 않아야 한다.

+ @RequestParam: HttpServletRequest의 파라미터를 가져올 때 사용한다. 각 변수별로 데이터를 저장할 수 있다는 장점이 있다. (getParameter와 유사함)
+ @PathVariable: 예를 들어 localhost:8080/{id}에서 id에 매핑되는 변수를 불러온다.
+ @RequestBody: 전송되는 파라미터를 한번에 가져올 때 사용한다. 객체로 바인딩할 수 있는 장점이 있다. 이를 위해서는 getter가 필요하다.
+ @ModelAttribute: 

### 3.5 타임리프의 selectbox와 checkbox 사용 방법

서버에서 페이지와 Model 객체를 넘겨줄 때, Model 객체의 attribute 상태에 따라 checkbox 또는 selectbox를 지정되도록 할 수 있다.

아래와 같은 타임리프 문법을 사용할 수 있다.

roleList를 loop하면서 resource.roleSet에 존재하는 것이라면 체크박스에 체크하는 방식으로 구현된다.
```html
          <select class="select" name="roleName" id="roleName">
               <option th:each="role: ${roleList}"
                       th:value="${role.roleName}"
                       th:text="${role.roleName}"
                       th:selected="${resource.roleSet.contains(role)} ? 'true' : 'false'"/>
          </select>
```
```java
    model.addAttribute("roleList", roleList);
    model.addAttribute("resource", resource);
```

### 3.6 Resource - List\<Role> 검색 쿼리 최적화

Resource와 Role은 서로 다대다이다. Join용 RoleResource 테이블을 만들어 이를 1:N, N:1으로 나타낼 수 있다.

Resource, RoleResource, Role 엔티티는 아래와 같이 연관관계를 맺고 있다.

```java
public class Resource {
    @OneToMany(mappedBy = "resource", cascade = CascadeType.REMOVE)
    private List<RoleResource> roleResources = new ArrayList<>();
}

public class RoleResource {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private Resource resource;
}

public class Role {
    @OneToMany(mappedBy = "role")
    private List<RoleResource> roleResourceList;
}
```

Resource 엔티티를 기준으로, 특정 Resource에 접근할 수 있는 모든 Role을 아래와 같이 찾을 수 있다.

```java
    Resource resource = resourceRepository.findById(id); // Query 1번 발생
    List<RoleResource> roleResources = resource.getRoleResources(); // Query 1번 발생  
    
    List<Role> roles = new ArrayList<>();
    for(RoleResource roleResource : roleResources) {
        role.add(roleResource.getRole()); // RoleResource 개수(N개)만큼 Query 발생
    }
```
N+1 문제가 발생했다. 물론, roleResource를 검색할 때 Role을 fetch join해서 Query 횟수를 줄일 수 있다.

그러나, 아래와 RoleResource를 기준으로 Role과 Resource를 fetch join해서 검색하면 Query를 1번으로 최적화할 수 있다.

```java
    Set<Role> currentRoles = new HashSet<>();
    for(RoleResource roleResource :  resourceService.findRoleResourcesWithFetch(resourceDto.getId())) {
        currentRoles.add(roleResource.getRole());
    }
    
    // Query
    @Repository("roleResourceRepository")
    public interface ResourceRoleRepository extends JpaRepository<RoleResource, Long> {

         @Query(value = "SELECT role_resource " +
                          "FROM RoleResource role_resource " +
                          "JOIN FETCH role_resource.role role " +
                          "LEFT JOIN FETCH role_resource.resource resource " +
                          "WHERE resource.id = :resourceId")
         List<RoleResource> findRoleResourcesWithFetch(@Param("resourceId")Long resourceId);
    }
```
### 3.7 requestMap 순서 중요성

SecurityMetaDataSource에는 requestMap<requestMatcher, list<ConfigAttribute>>(url, 접근 가능한 권한 리스트)가 존재한다.

특정 url로 접근할 때, 아래의 로직으로 검색한다.

```java
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        // requestMap.put(new AntPathRequestMatcher("/mypage"), Arrays.asList(new SecurityConfig("ROLE_USER")));
        if(requestMap != null) {
            for(Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet() ) {
                RequestMatcher matcher = entry.getKey();
                if(matcher.matches(request)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
```

위의 코드를 분석해보면 requestMap을 순서대로 검색한다. 그러므로, requestMap에는 구체적인 경로가 더 먼저 들어가도록 만들어야 한다.

예를 들어, 1. /admin/user -> admin, user, 2. /admin/** -> admin 순서로 저장되었다고 가정하자.

이 경우에 user 권한을 가진 사용자가 /admin/user로 접근할 수 있다.

그러나, 1. /admin/** -> admin, 2. /admin/user -> admin, user 순서로 저장되었다고 가정하자.

이 경우에는 /admin/** 규칙에 의해 /admin/user에 접근할 수 없다. 

그러므로, requestMap은 구체적인 경로가 먼저 저장될 수 있도록 순서를 조정하는 것이 중요하다.
