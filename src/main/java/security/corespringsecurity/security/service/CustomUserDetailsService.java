package security.corespringsecurity.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import security.corespringsecurity.domain.entity.Account;
import security.corespringsecurity.domain.entity.AccountRole;
import security.corespringsecurity.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service("UserDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    // 이 클래스 자체를 UserService 안에서 오버라이딩 하는 방법도 있다.
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = userRepository.findByUsername(username);

        if(account == null) {
            throw new UsernameNotFoundException("UsernameNotFoundException");
        }
        List<GrantedAuthority> roles = new ArrayList<>();

        List<AccountRole> accountRoleList = account.getAccountRoleList();
        for(AccountRole accountRole: accountRoleList) {
            roles.add(new SimpleGrantedAuthority(accountRole.getRole().getRoleName()));
        }

        AccountContext accountContext = new AccountContext(account, roles);
        return accountContext;
    }
}
