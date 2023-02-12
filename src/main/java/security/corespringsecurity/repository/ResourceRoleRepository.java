package security.corespringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import security.corespringsecurity.domain.entity.AccountRole;
import security.corespringsecurity.domain.entity.Role;
import security.corespringsecurity.domain.entity.RoleResource;

import java.util.List;

@Repository("roleResourceRepository")
public interface ResourceRoleRepository extends JpaRepository<RoleResource, Long> {
    @Query(value = "SELECT rr FROM RoleResource rr INNER JOIN rr.resource r "
            +"WHERE r.id = :resourceId")
    List<RoleResource> findResourceRoles(@Param("resourceId")Long resourceId);


    @Query(value = "SELECT role_resource " +
                    "FROM RoleResource role_resource " +
                    "JOIN FETCH role_resource.role role " +
                    "LEFT JOIN FETCH role_resource.resource resource " +
                    "WHERE resource.id = :resourceId " +
                    "ORDER BY resource.orderNum")
    List<RoleResource> findRoleResourcesWithFetch(@Param("resourceId")Long resourceId);

    @Query(value = "SELECT role_resource " +
                    "FROM RoleResource role_resource " +
                    "JOIN FETCH role_resource.role role " +
                    "LEFT JOIN FETCH role_resource.resource resource " +
                    "ORDER BY resource.orderNum")
    List<RoleResource> findAllRoleResourcesWithFetch();


}
