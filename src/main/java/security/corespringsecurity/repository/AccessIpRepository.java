package security.corespringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import security.corespringsecurity.domain.entity.AccessIp;

public interface AccessIpRepository extends JpaRepository<AccessIp, Long> {
    AccessIp findByIpAddress(String IpAddress);
}
