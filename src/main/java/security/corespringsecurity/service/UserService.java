package security.corespringsecurity.service;

import security.corespringsecurity.domain.entity.Account;
import security.corespringsecurity.domain.entity.AccountRole;
import security.corespringsecurity.domain.entity.Role;

import java.util.List;

public interface UserService {

    void create(Account account);

    List<Account> findAll();
    Account findById(Long id);

    List<AccountRole> findAccountRoles(Long accountId);
    void deleteAccountRole(AccountRole accountRole);

    void linkAccountRoles(Long accountId, List<String> roleNames);
}
