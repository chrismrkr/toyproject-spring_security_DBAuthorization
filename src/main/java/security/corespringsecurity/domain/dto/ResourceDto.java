package security.corespringsecurity.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import security.corespringsecurity.domain.entity.Role;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceDto {
    private Long id;
    private String resourceName;
    private String resourceType;
    private String httpMethod;
    private Integer orderNum;
    private String roleName;
    private Set<Role> roleSet;
}
