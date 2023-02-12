package security.corespringsecurity.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import security.corespringsecurity.domain.entity.Account;
import security.corespringsecurity.domain.entity.Role;
import security.corespringsecurity.service.RoleService;

import javax.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    @GetMapping("/admin/roles")
    public String roleListController(Model model) {
        List<Role> roles = roleService.findAll();
        model.addAttribute("roles", roles);
        return "/admin/role/list";
    }

    @GetMapping("/admin/roles/register")
    public String addRoleForm(Model model) {
        Role role = Role.builder().build();
        model.addAttribute("role", role);
        return "/admin/role/detail";
    }
    @PostMapping("/admin/roles/register")
    public String addRole(@RequestParam("roleName") String roleName,
                          @RequestParam("roleDescription") String roleDescription) {
        Role role = Role.builder().roleName(roleName)
                .roleDescription(roleDescription).build();
        roleService.create(role);
        return "redirect:/admin/roles";
    }

    @GetMapping("/admin/roles/{id}")
    public String roleDetailController(@PathVariable Long id, Model model) {
        Role role = roleService.findById(id);
        model.addAttribute("role", role);
        return "admin/role/detail";
    }
    @PostMapping("/admin/roles/{id}")
    @Transactional
    public String editRole(@PathVariable Long id, @RequestParam("roleName") String roleName,
                           @RequestParam("roleDescription") String roleDescription) {
        Role role = roleService.findById(id);
        role.editRoleNameDescription(roleName, roleDescription);
        return "redirect:/admin/roles";
    }

}
