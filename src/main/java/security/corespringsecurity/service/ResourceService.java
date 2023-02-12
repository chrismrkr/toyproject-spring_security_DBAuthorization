package security.corespringsecurity.service;


import security.corespringsecurity.domain.entity.AccountRole;
import security.corespringsecurity.domain.entity.Resource;
import security.corespringsecurity.domain.entity.Role;
import security.corespringsecurity.domain.entity.RoleResource;

import java.util.List;

public interface ResourceService {
    void create(Resource resource);
    List<Resource> findAll();
    List<RoleResource> findRoleResources(Long resourceId);
    List<RoleResource> findRoleResourcesWithFetch(Long resourceId);

    Resource findById(Long id);
    Resource findByIdWithRoleResource(Long id);

    void deleteRoleResource(RoleResource roleResource);
    void delete(Resource resource);
}
