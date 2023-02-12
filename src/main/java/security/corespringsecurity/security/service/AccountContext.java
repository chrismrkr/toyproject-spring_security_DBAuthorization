package security.corespringsecurity.security.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import security.corespringsecurity.domain.entity.Account;

import java.util.Collection;

public class AccountContext extends User {
    private final Account account;
    public AccountContext(Account account, Collection<? extends GrantedAuthority> authorities) {
        super(account.getUsername(), account.getPassword(), authorities);
        this.account = account;
    }

    public Account getAccount() {
        return this.account;
    }
}
