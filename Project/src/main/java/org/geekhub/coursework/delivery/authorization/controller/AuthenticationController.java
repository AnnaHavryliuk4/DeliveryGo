package org.geekhub.coursework.delivery.authorization.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.geekhub.coursework.delivery.authorization.entities.User;
import org.geekhub.coursework.delivery.authorization.service.UserService;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("user") @Valid User user,
                               BindingResult result,
                               Model model) {

        if (!user.isPasswordMatch(user.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "passwordMatch", "Passwords do not match");
            model.addAttribute("passwordMatch", "Passwords do not match");
        }
        if (!user.isPasswordDuplicated()) {
            result.rejectValue("password", "passwordDuplicate", "The password cannot duplicate the user's login");
            model.addAttribute("passwordDuplicate", "The password cannot duplicate the user's login");
        }
        try {
            if (!userService.isEmailUnique(user.getEmail())) {
                result.rejectValue("email", "emailExists", "Email already registered");
                model.addAttribute("emailExistsError", "Email already registered");
            }
            if (!userService.isUsernameUnique(user.getUsername())) {
                result.rejectValue("username", "usernameExists", "Username already registered");
            }
        } catch (IncorrectResultSizeDataAccessException ex) {
            result.rejectValue("username", "multipleUsersFound", "User with this username already exists");
        }

        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());

            return "/registration";
        } else {
            userService.saveUser(user);
            return "redirect:/";
        }
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") User user, BindingResult bindingResult, Model model, HttpSession session) {

        if (!userService.authenticateUser(user.getEmail(), user.getPassword())) {
            bindingResult.rejectValue("email", "error.invalidEmail", "Invalid email address");
            model.addAttribute("errorMessage", "Invalid email or password");
        }

        if (bindingResult.hasErrors()) {
            return "login";
        }
        Long userId = user.getId();
        session.setAttribute("userId", userId);
        return "mainPage";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }
}
