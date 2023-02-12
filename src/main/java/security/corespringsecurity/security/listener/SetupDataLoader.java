package security.corespringsecurity.security.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import security.corespringsecurity.domain.entity.AccessIp;
import security.corespringsecurity.domain.entity.Resource;
import security.corespringsecurity.domain.entity.Role;
import security.corespringsecurity.domain.entity.RoleHierarchy;
import security.corespringsecurity.repository.*;

import javax.transaction.Transactional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private RoleHierarchyRepository roleHierarchyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccessIpRepository accessIpRepository;

    private static AtomicInteger count = new AtomicInteger(0);
    // 애플리케이션이 시작될 때, 생성하고자 하는 부분을 담음
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(alreadySetup) { return; }
        setSecurityResources();

        alreadySetup = true;
    }

    private void setSecurityResources() {
        Role roleAdmin = roleRepository.findByRoleName("ROLE_ADMIN");
        Role roleManager = roleRepository.findByRoleName("ROLE_MANAGER");
        Role roleUser = roleRepository.findByRoleName("ROLE_USER");
        createRoleHierarchyIfNotFound(roleManager, roleAdmin);
        createRoleHierarchyIfNotFound(roleUser, roleManager);
    }

    @Transactional
    public void createRoleHierarchyIfNotFound(Role childRole, Role parentRole) {
        RoleHierarchy parentRoleHierarchy = roleHierarchyRepository.findByChildName(parentRole.getRoleName());
        RoleHierarchy childRoleHierarchy = roleHierarchyRepository.findByChildName(childRole.getRoleName());
        if (parentRoleHierarchy == null) {
            parentRoleHierarchy = RoleHierarchy.builder()
                    .childName(parentRole.getRoleName())
                    .build();
        }
        if (childRoleHierarchy == null) {
            childRoleHierarchy = RoleHierarchy.builder()
                    .childName(childRole.getRoleName())
                    .build();
        }
        parentRoleHierarchy = roleHierarchyRepository.save(parentRoleHierarchy);
        childRoleHierarchy.setParentName(parentRoleHierarchy);
        roleHierarchyRepository.save(childRoleHierarchy);
    }

    private void setupAccessIpData() {
        AccessIp byIpAddress = accessIpRepository.findByIpAddress("0:0:0:0:0:0:0:1");
        if(byIpAddress == null) {
            AccessIp accessIp = AccessIp.builder().ipAddress("0:0:0:0:0:0:0:1").build();
            accessIpRepository.save(accessIp);
        }
    }
}
