package security.corespringsecurity.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import security.corespringsecurity.domain.entity.RoleHierarchy;
import security.corespringsecurity.repository.RoleHierarchyRepository;
import security.corespringsecurity.service.RoleHierarchyService;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleHierarchyServiceImpl implements RoleHierarchyService {
    private final RoleHierarchyRepository roleHierarchyRepository;

    @Override
    @Transactional
    public String findAllHierarchy() {
        List<RoleHierarchy> roleHierarchyList = roleHierarchyRepository.findAll();
        StringBuilder stringBuilder = new StringBuilder();

        for(RoleHierarchy roleHierarchy : roleHierarchyList) {
            if(roleHierarchy.getParentName() != null) {
                stringBuilder.append(roleHierarchy.getParentName().getChildName());
                stringBuilder.append(" > ");
                stringBuilder.append(roleHierarchy.getChildName());
                stringBuilder.append("\n");
            }
        }
        log.info(stringBuilder.toString());
        return stringBuilder.toString();
    }
}
