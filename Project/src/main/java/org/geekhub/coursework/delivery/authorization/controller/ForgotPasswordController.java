package org.geekhub.coursework.delivery.authorization.controller;

import org.geekhub.coursework.delivery.authorization.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {

    private final UserService userService;

    public ForgotPasswordController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPasswordForm(@RequestParam("email") String email, Model model) {
        try {
            String message = userService.forgotPass(email);
            model.addAttribute("success", message);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);

        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPasswordForm(@RequestParam("token") String token, @RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword, Model model) {
        try {
            userService.resetPass(token, password, confirmPassword);
            return "/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/reset-password?token=" + token;
        }
    }

}
