package security.corespringsecurity.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import security.corespringsecurity.domain.entity.Resource;
import security.corespringsecurity.domain.entity.Role;
import security.corespringsecurity.domain.entity.RoleResource;
import security.corespringsecurity.repository.AccountRoleRepository;
import security.corespringsecurity.repository.ResourceRoleRepository;
import security.corespringsecurity.repository.RoleRepository;
import security.corespringsecurity.service.RoleService;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

@Service("roleService")
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final ResourceRoleRepository resourceRoleRepository;

    @Transactional
    @Override
    public void create(Role role) {
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void linkRoleResource(Role role, Resource resource) {
        RoleResource roleResource = RoleResource.builder().role(role).resource(resource).build();
        resourceRoleRepository.save(roleResource);
        role.getRoleResourceList().add(roleResource);
        resource.getRoleResources().add(roleResource);
    }


    @Override
    public List<Role> findAll() {
        List<Role> roles = roleRepository.findAll();
        return roles;
    }

    @Override
    public Role findById(Long id) {
        Role role = roleRepository.findById(id).get();
        return role;
    }

    @Override
    public Role findByRoleName(String roleName) {
        Role role = roleRepository.findByRoleName(roleName);
        return role;
    }

    @PostConstruct
    @Transactional
    private void createInitRoles() {
        Role user = Role.builder().roleDescription("USER").roleName("ROLE_USER").build();
        Role admin = Role.builder().roleDescription("ADMIN").roleName("ROLE_ADMIN").build();
        Role manager = Role.builder().roleDescription("MANAGER").roleName("ROLE_MANAGER").build();
        roleRepository.save(user);
        roleRepository.save(admin);
        roleRepository.save(manager);
    }
}
