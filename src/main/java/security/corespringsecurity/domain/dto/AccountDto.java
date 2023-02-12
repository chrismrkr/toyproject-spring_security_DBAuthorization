package security.corespringsecurity.domain.dto;

import lombok.Data;

@Data
public class AccountDto {
    private String username;
    private String password;
    private String email;
    private String age;
}
