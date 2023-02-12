package security.corespringsecurity.service;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.Map;

public interface SecurityResourceService {
    Map<RequestMatcher, List<ConfigAttribute>> getResourceList();

    List<String> getAccessIpList();
}
