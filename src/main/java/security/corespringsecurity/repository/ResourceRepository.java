package security.corespringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import security.corespringsecurity.domain.entity.Resource;

import java.util.List;

@Repository("resourceRepository")
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    @Query(value = "SELECT r FROM Resource r " +
                    "JOIN FETCH r.roleResources " +
                    "WHERE r.id = :resourceId")
    Resource findByIdWithRoleResource(@Param("resourceId") Long resourceId);

}
