package security.corespringsecurity.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue
    @Column(name="role_id")
    private Long id;
    private String roleDescription;
    private String roleName;
    public static Builder builder() {
        return new Builder();
    }
    private Role(Builder builder) {
        this.roleDescription = builder.roleDescription;
        this.roleName = builder.roleName;
        this.accountRoleList = new ArrayList<>();
        this.roleResourceList = new ArrayList<>();
    }

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<AccountRole> accountRoleList;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<RoleResource> roleResourceList;

    public void editRoleNameDescription(String roleName, String roleDescription) {
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }
    public static class Builder {
        private String roleDescription;
        private String roleName;

        public Builder roleDescription(String roleDescription) {
            this.roleDescription = roleDescription;
            return this;
        }

        public Builder roleName(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public Role build() {
            return new Role(this);
        }
    }
}
