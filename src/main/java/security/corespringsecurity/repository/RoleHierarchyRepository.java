package security.corespringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import security.corespringsecurity.domain.entity.RoleHierarchy;

@Repository("roleHierarchyRepository")
public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {
    RoleHierarchy findByChildName(String childName);
}
