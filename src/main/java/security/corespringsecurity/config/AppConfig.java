package security.corespringsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import security.corespringsecurity.repository.AccessIpRepository;
import security.corespringsecurity.repository.ResourceRepository;
import security.corespringsecurity.service.SecurityResourceService;
import security.corespringsecurity.service.impl.SecurityResourceServiceImpl;

@Configuration
public class AppConfig {

    @Bean
    public SecurityResourceService securityResourceService(ResourceRepository resourceRepository, AccessIpRepository accessIpRepository) {
        return new SecurityResourceServiceImpl(resourceRepository, accessIpRepository);
    }
}
