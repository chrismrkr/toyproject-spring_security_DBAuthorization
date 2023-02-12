package security.corespringsecurity.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Columns;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@EqualsAndHashCode
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    @Column(name="account_id")
    private Long id;
    private String username;
    private String password;
    private String email;
    private String age;

    public static Builder builder() {
        return new Builder();
    }
    private Account(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.age = builder.age;
        this.accountRoleList = new ArrayList<>();
    }

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<AccountRole> accountRoleList;


    public static class Builder {
        private String username;
        private String password;
        private String email;
        private String age;
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        public Builder password(String password) {
            this.password = password;
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
        public Account build() {
            return new Account(this);
        }
    }
}

/* Lombok의 @Data 애노테이션 기능
    Getter, Setter RequiredArgsConstructor, ToString, EqualsAndHashCode 애노테이션.
 */
