package security.corespringsecurity.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import security.corespringsecurity.domain.entity.Resource;
import security.corespringsecurity.domain.entity.Role;
import security.corespringsecurity.domain.entity.RoleResource;
import security.corespringsecurity.repository.ResourceRepository;
import security.corespringsecurity.repository.ResourceRoleRepository;
import security.corespringsecurity.repository.RoleRepository;
import security.corespringsecurity.service.ResourceService;
import security.corespringsecurity.service.RoleService;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

@Service("resourceService")
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceRoleRepository resourceRoleRepository;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public void create(Resource resource) {
        resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public List<Resource> findAll() {
        List<Resource> resources = resourceRepository.findAll();
        return resources;
    }

    @Override
    public List<RoleResource> findRoleResources(Long resourceId) {
        List<RoleResource> resourceRoles = resourceRoleRepository.findResourceRoles(resourceId);
        return resourceRoles;
    }

    @Override
    public List<RoleResource> findRoleResourcesWithFetch(Long resourceId) {
        List<RoleResource> roleResourcesWithFetch = resourceRoleRepository.findRoleResourcesWithFetch(resourceId);
        return roleResourcesWithFetch;
    }

    @Override
    @Transactional
    public Resource findById(Long id) {
        Resource resource = resourceRepository.findById(id).get();
        return resource;
    }

    @Override
    public Resource findByIdWithRoleResource(Long id) {
        Resource resource = resourceRepository.findByIdWithRoleResource(id);
       // return byIdWithRoleResource.get(0);
        return resource;
    }

    @Override
    @Transactional
    public void deleteRoleResource(RoleResource roleResource) {
        resourceRoleRepository.delete(roleResource);
    }

    @Override
    @Transactional
    public void delete(Resource resource) {
        resourceRepository.delete(resource);
    }

    /*
    @PostConstruct
    @DependsOn("roleService")
    private void createInitResources() {
        Resource myPage = Resource.builder()
                .resourceName("/mypage")
                .orderNum(1)
                .resourceType("url")
                .httpMethod("GET")
                .build();
        Resource messages = Resource.builder()
                .resourceName("/messages")
                .orderNum(1)
                .resourceType("url")
                .httpMethod("GET")
                .build();
        Resource config = Resource.builder()
                .resourceName("/config")
                .orderNum(1)
                .resourceType("url")
                .httpMethod("GET")
                .build();
        create(myPage);
        create(messages);
        create(config);

        Role roleUser = roleRepository.findByRoleName("ROLE_USER");
        Role roleAdmin = roleRepository.findByRoleName("ROLE_ADMIN");
        Role roleManager = roleRepository.findByRoleName("ROLE_MANAGER");

        RoleResource roleResource1 = RoleResource.builder().role(roleUser).resource(myPage).build();
        resourceRoleRepository.save(roleResource1);
        roleUser.getRoleResourceList().add(roleResource1);
        myPage.getRoleResources().add(roleResource1);

        RoleResource roleResource2 = RoleResource.builder().role(roleManager).resource(messages).build();
        resourceRoleRepository.save(roleResource2);
        roleManager.getRoleResourceList().add(roleResource2);
        messages.getRoleResources().add(roleResource2);

        RoleResource roleResource3 = RoleResource.builder().role(roleAdmin).resource(config).build();
        resourceRoleRepository.save(roleResource3);
        roleAdmin.getRoleResourceList().add(roleResource3);
        config.getRoleResources().add(roleResource3);
    }
    */
}
