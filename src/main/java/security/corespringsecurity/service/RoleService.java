package security.corespringsecurity.service;

import security.corespringsecurity.domain.entity.Resource;
import security.corespringsecurity.domain.entity.Role;

import java.util.List;

public interface RoleService {
    void create(Role role);
    List<Role> findAll();
    Role findById(Long id);
    Role findByRoleName(String roleName);

    void linkRoleResource(Role role, Resource resource);
}
