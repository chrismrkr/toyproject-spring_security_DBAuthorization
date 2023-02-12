package security.corespringsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import security.corespringsecurity.domain.entity.Account;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
 /*
    Spring Data Jpa에 대해서도 공부해보자.
    1. Spring Data JPA에서 CRUD는 내부적으로 어떻게 수행될까?
    2. 동시성 제어는 어떻게 하고 있을까?
  */
}
