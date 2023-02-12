package security.corespringsecurity.controller.admin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import security.corespringsecurity.domain.entity.Account;
import security.corespringsecurity.domain.entity.AccountRole;
import security.corespringsecurity.domain.entity.Role;
import security.corespringsecurity.service.RoleService;
import security.corespringsecurity.service.UserService;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserManagerController {
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/admin/users")
    public String userListController(Model model) {
        List<AccountVO> accountVOList = new ArrayList<>();
        for(Account account : userService.findAll()) {
            accountVOList.add(AccountVO.builder()
                            .id(account.getId())
                            .username(account.getUsername())
                            .age(account.getAge())
                            .email(account.getEmail())
                            .roles(account.getAccountRoleList())
                            .build()
                    );
        }
        model.addAttribute("accounts", accountVOList);
        return "admin/user/list";
    }

    @GetMapping("/admin/users/{id}")
    public String userDetailController(@PathVariable Long id, Model model) {
        Account account = userService.findById(id);
        model.addAttribute("account",
                                        AccountVO.builder()
                                                .id(account.getId())
                                                .username(account.getUsername())
                                                .age(account.getAge())
                                                .email(account.getEmail())
                                                .build());

        Map<String, String> roles = new LinkedHashMap<>();
        for(Role role: roleService.findAll()) {
            roles.put(role.getRoleName(), role.getRoleDescription());
        }
        model.addAttribute("roles", roles);
        return "admin/user/detail";
    }

    @PostMapping("/admin/users/{id}")
    public String modifyUserRoles(@PathVariable Long id, @RequestParam("roles") List<String> roles) {
        List<AccountRole> matchedAccountRoles = userService.findAccountRoles(id);
        for(AccountRole accountRole : matchedAccountRoles) {
            userService.deleteAccountRole(accountRole);
        }
        userService.linkAccountRoles(id, roles);
        return "redirect:/admin/users";
    }

    @Getter
    public static class AccountVO {
        private Long id;
        private String username;
        private String email;
        private String age;
        private String roles;
        public static Builder builder() {
            return new Builder();
        }
        private AccountVO(Builder builder) {
            this.id = builder.id;
            this.username = builder.username;
            this.email = builder.email;
            this.age = builder.age;
            this.roles = builder.roles;
        }
        public static class Builder {
            private Long id;
            private String username;
            private String email;
            private String age;
            private String roles;
            public Builder id(Long id) {
                this.id = id;
                return this;
            }
            public Builder username(String username) {
                this.username = username;
                return this;
            }
            public Builder email(String email) {
                this.email = email;
                return this;
            }
            public Builder age(String age) {
                this.age = age;
                return this;
            }
            public Builder roles(List<AccountRole> accountRoles) {
                StringBuilder stringBuilder = new StringBuilder();
                for(AccountRole accountRole : accountRoles) {
                    stringBuilder.append(accountRole.getRole().getRoleName());
                    stringBuilder.append(" ");
                }
                this.roles = stringBuilder.toString();
                return this;
            }
            public AccountVO build() {
                return new AccountVO(this);
            }
        }
    }
}
