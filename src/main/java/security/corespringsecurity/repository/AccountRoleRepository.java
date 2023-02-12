package security.corespringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import security.corespringsecurity.domain.entity.Account;
import security.corespringsecurity.domain.entity.AccountRole;
import security.corespringsecurity.domain.entity.Role;

import java.util.List;


@Repository("accountRoleRepository")
public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
    @Query(value = "SELECT r FROM AccountRole r INNER JOIN r.account a "
                    +"WHERE a.id = :accountId")
    List<AccountRole> findAccountRoles(@Param("accountId")Long accountId);

}
