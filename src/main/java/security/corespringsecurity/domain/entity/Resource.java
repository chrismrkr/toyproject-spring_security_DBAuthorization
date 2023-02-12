package security.corespringsecurity.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Resource {
    @Id
    @GeneratedValue
    @Column(name="resource_id")
    private Long id;
    private String httpMethod;
    private Integer orderNum;
    private String resourceName;
    private String resourceType;

    public static Builder builder() {
        return new Builder();
    }
    private Resource(Builder builder) {
        this.httpMethod = builder.httpMethod;
        this.orderNum = builder.orderNum;
        this.resourceName = builder.resourceName;
        this.resourceType = builder.resourceType;
    }

    @OneToMany(mappedBy = "resource", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<RoleResource> roleResources = new ArrayList<>();

    public static class Builder {
        private String httpMethod;
        private Integer orderNum;
        private String resourceName;
        private String resourceType;

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }
        public Builder orderNum(Integer orderNum) {
            this.orderNum = orderNum;
            return this;
        }
        public Builder resourceName(String resourceName) {
            this.resourceName = resourceName;
            return this;
        }
        public Builder resourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }
        public Resource build() {
            return new Resource(this);
        }
    }
}
