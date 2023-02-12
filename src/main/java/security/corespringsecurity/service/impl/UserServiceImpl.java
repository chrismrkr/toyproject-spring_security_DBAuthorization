package security.corespringsecurity.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import security.corespringsecurity.domain.entity.Account;
import security.corespringsecurity.domain.entity.AccountRole;
import security.corespringsecurity.domain.entity.Role;
import security.corespringsecurity.repository.AccountRoleRepository;
import security.corespringsecurity.repository.RoleRepository;
import security.corespringsecurity.repository.UserRepository;
import security.corespringsecurity.service.UserService;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service("userService")
@RequiredArgsConstructor
@DependsOn("roleService")
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void create(Account account) {
        userRepository.save(account);
        Role initRole = roleRepository.findByRoleName("ROLE_USER");
        AccountRole accountRole = AccountRole.builder()
                                    .account(account)
                                    .role(initRole).build();
        accountRoleRepository.save(accountRole);
        account.getAccountRoleList().add(accountRole);
    }

    @Override
    public List<Account> findAll() {
        List<Account> allAccounts = userRepository.findAll();
        return allAccounts;
    }

    @Override
    public Account findById(Long id) {
        Account account = userRepository.findById(id).get();
        return account;
    }

    @Override
    public List<AccountRole> findAccountRoles(Long accountId) {
        List<AccountRole> accountRoles = accountRoleRepository.findAccountRoles(accountId);
        return accountRoles;
    }


    @Override
    @Transactional
    public void deleteAccountRole(AccountRole accountRole) {
        accountRoleRepository.delete(accountRole);
    }

    @Override
    @Transactional
    public void linkAccountRoles(Long accountId, List<String> roleNames) {
        Account account = userRepository.findById(accountId).get();
        for(String roleName : roleNames) {
            Role role = roleRepository.findByRoleName(roleName);
            AccountRole accountRole = AccountRole.builder().account(account).role(role).build();
            accountRoleRepository.save(accountRole);
            account.getAccountRoleList().add(accountRole);
            role.getAccountRoleList().add(accountRole);
        }
    }

    @PostConstruct
    @Transactional
    private void createInitAccounts() {
        Account user = Account.builder().
                username("user")
                .password(passwordEncoder.encode("1111"))
                .age("23")
                .email("user@dot.com")
                .build();
        Account admin = Account.builder()
                .username("admin")
                .password(passwordEncoder.encode("1111"))
                .age("45")
                .email("admin@dot.com")
                .build();
        create(user);
        create(admin);
    }
}
