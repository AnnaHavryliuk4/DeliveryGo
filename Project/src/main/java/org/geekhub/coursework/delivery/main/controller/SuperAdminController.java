package org.geekhub.coursework.delivery.main.controller;

import org.geekhub.coursework.delivery.authorization.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SuperAdminController {

    private final UserService userService;

    public SuperAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/superadmin")
    public String adminPage() {
        return "super_admin_page";
    }

    @GetMapping("/superadmin/manage_users")
    public String allUsersGet(Model model) {
        model.addAttribute("users", userService.getAllUsersWithAdminAndUserRoles());

        return "manage_users";
    }

    @PostMapping("/superadmin/change_role")
    public String changeRole(@RequestParam Long userId,
                             @RequestParam Long roleId) {
        userService.changeUserRole(userId, roleId);
        return "redirect:/superadmin/manage_users";
    }
}
