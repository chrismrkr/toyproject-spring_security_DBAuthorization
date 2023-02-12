package security.corespringsecurity.controller.admin;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import security.corespringsecurity.domain.dto.ResourceDto;
import security.corespringsecurity.domain.entity.Resource;
import security.corespringsecurity.domain.entity.Role;
import security.corespringsecurity.domain.entity.RoleResource;
import security.corespringsecurity.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import security.corespringsecurity.service.ResourceService;
import security.corespringsecurity.service.RoleService;

import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ResourceController {
    private final ResourceService resourceService;
    private final RoleService roleService;
    private final UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    @GetMapping("/admin/resources")
    public String resourceListController(Model model) {
        List<Resource> resources = resourceService.findAll();
        model.addAttribute("resources", resources);
        return "/admin/resource/list";
    }

    @GetMapping("/admin/resources/register")
    public String addResourceForm(Model model) {
        List<Role> roleList = roleService.findAll();
        model.addAttribute("roleList", roleList);

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role());
        ResourceDto resourceDto = ResourceDto.builder().roleSet(roleSet).build();
        model.addAttribute("resource", resourceDto);

        return "/admin/resource/detail";
    }

    @PostMapping("/admin/resources/register")
    public String addResource(ResourceDto resourceDto) {
        // 새로운 권한 추가 및 Role - Resource 연관관계 매핑
        Resource resource = Resource.builder()
                .resourceName(resourceDto.getResourceName())
                .resourceType(resourceDto.getResourceType())
                .httpMethod(resourceDto.getHttpMethod())
                .orderNum(resourceDto.getOrderNum()).build();
        resourceService.create(resource);

        Role role = roleService.findByRoleName(resourceDto.getRoleName());
        roleService.linkRoleResource(role, resource);

        urlFilterInvocationSecurityMetadataSource.reload();

        return "redirect:/admin/resources";
    }

    @GetMapping("/admin/resources/{id}")
    @Transactional
    public String editResourceForm(@PathVariable Long id, Model model) {
        List<Role> roleList = roleService.findAll();
        model.addAttribute("roleList", roleList);


        List<RoleResource> roleResources = resourceService.findRoleResourcesWithFetch(id);
        Resource resource = roleResources.get(0).getResource();

        Set<Role> roleSet = new HashSet<>();
        for(RoleResource roleResource : roleResources) {
            roleSet.add(roleResource.getRole());
        }

        ResourceDto resourceDto = ResourceDto.builder()
                .id(resource.getId())
                        .resourceName(resource.getResourceName())
                                .resourceType(resource.getResourceType())
                .httpMethod(resource.getHttpMethod())
                        .orderNum(resource.getOrderNum())
                                .roleName(roleResources.get(0).getRole().getRoleName())
                                        .roleSet(roleSet)
                                                .build();
        model.addAttribute("resource", resourceDto);

        return "/admin/resource/detail";
    }

    @PostMapping("/admin/resources/{id}")
    @Transactional
    public String editResource(ResourceDto resourceDto) {
        /* ASIS. Role - Resource 연관관계는 그대로 두고, resource의 필드(속성)만 변경
        ** TOBE.
        ** case 1 : resource 필드 변경 & role 불변 => 기존 Resource만 변경
        ** case 2 : resource 필드 불변 & role 변경 => 기존 Resource의 roles List에 추가 및 연관관계 매핑
        ** case 3 : resource 필드 변경 => 새로운 Resource 추가 후 연관관계 매핑
        */
        Set<Role> currentRoles = new HashSet<>();
        for(RoleResource roleResource :  resourceService.findRoleResourcesWithFetch(resourceDto.getId())) {
            currentRoles.add(roleResource.getRole());
        }
        Role newRole = roleService.findByRoleName(resourceDto.getRoleName());


        Resource resource = resourceService.findById(resourceDto.getId());
        if(isNewResource(resource, resourceDto)) {
            if(isExistRole(newRole, currentRoles)) { // case 1: 필드만 변경
                resource.setResourceName(resourceDto.getResourceName());
                resource.setResourceType(resourceDto.getResourceType());
                resource.setHttpMethod(resourceDto.getHttpMethod());
                resource.setOrderNum(resourceDto.getOrderNum());
            }
            else { // case 3: resource 신규 추가
                Resource newResource = Resource.builder().resourceName(resourceDto.getResourceName())
                        .resourceType(resourceDto.getResourceType())
                        .httpMethod(resourceDto.getHttpMethod())
                        .orderNum(resourceDto.getOrderNum()).build();
                resourceService.create(newResource);

                Role role = roleService.findByRoleName(resourceDto.getRoleName());
                roleService.linkRoleResource(role, newResource);
            }
        }

        else {
            if(!isExistRole(newRole, currentRoles)) { // case 2: 연관관계만 새롭게 매핑
                roleService.linkRoleResource(newRole, resource);
            }
        }

        urlFilterInvocationSecurityMetadataSource.reload();
        return "redirect:/admin/resources";
    }
    private boolean isNewResource(Resource oldResource, ResourceDto newResource) {
        boolean notChange =  oldResource.getResourceName().equals(newResource.getResourceName()) &&
                    oldResource.getResourceType().equals(newResource.getResourceType()) &&
                    oldResource.getHttpMethod().equals(newResource.getHttpMethod()) &&
                    (oldResource.getOrderNum() == newResource.getOrderNum());
        return notChange ? false: true;
    }
    private boolean isExistRole(Role role, Set<Role> roleSet) {
        return roleSet.contains(role) ? true: false;
    }

    @GetMapping("/admin/resources/delete/{id}")
    public String deleteResource(@PathVariable("id") Long id) {
        Resource resource = resourceService.findById(id);
        resourceService.delete(resource);
        urlFilterInvocationSecurityMetadataSource.reload();
        return "redirect:/admin/resources";
    }
}
