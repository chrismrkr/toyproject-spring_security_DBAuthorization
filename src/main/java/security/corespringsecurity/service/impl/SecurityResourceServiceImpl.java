package security.corespringsecurity.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import security.corespringsecurity.domain.entity.Resource;
import security.corespringsecurity.domain.entity.Role;
import security.corespringsecurity.domain.entity.RoleResource;
import security.corespringsecurity.repository.AccessIpRepository;
import security.corespringsecurity.repository.ResourceRepository;
import security.corespringsecurity.repository.ResourceRoleRepository;
import security.corespringsecurity.service.SecurityResourceService;

import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class SecurityResourceServiceImpl implements SecurityResourceService {
    private final ResourceRepository resourceRepository;
    private final AccessIpRepository accessIpRepository;
    @Override
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();

        List<Resource> resourceList = resourceRepository.findAll();
        resourceList.forEach(resource -> {
            List<ConfigAttribute> configAttributes = new ArrayList<>();
            for(RoleResource roleResource : resource.getRoleResources()) {
                Role role = roleResource.getRole();
                configAttributes.add(new SecurityConfig(role.getRoleName()));
            }
            result.put(new AntPathRequestMatcher(resource.getResourceName()), configAttributes);
        });

        return result;
    }

    @Override
    public List<String> getAccessIpList() {
        List<String> collect = accessIpRepository.findAll().stream().map(accessIp -> accessIp.getIpAddress()).collect(Collectors.toList());
        return collect;
    }
}
