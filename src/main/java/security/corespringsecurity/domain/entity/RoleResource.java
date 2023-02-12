package security.corespringsecurity.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity @Getter
@EqualsAndHashCode
@NoArgsConstructor
public class RoleResource {
    @Id
    @GeneratedValue
    @Column(name="role_resource_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    public static Builder builder() {
        return new Builder();
    }
    private RoleResource(Builder builder) {
        this.role = builder.role;
        this.resource = builder.resource;
    }
    public static class Builder {
        private Role role;
        private Resource resource;
        public Builder() {}
        public Builder role(Role role) {
            this.role = role;
            return this;
        }
        public Builder resource(Resource resource) {
            this.resource = resource;
            return this;
        }
        public RoleResource build() {
            return new RoleResource(this);
        }
    }
}
