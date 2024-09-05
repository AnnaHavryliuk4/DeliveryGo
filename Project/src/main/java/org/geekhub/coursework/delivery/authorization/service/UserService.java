package org.geekhub.coursework.delivery.authorization.service;

import org.geekhub.coursework.delivery.authorization.entities.User;
import org.geekhub.coursework.delivery.authorization.controller.UtilsToken;
import org.geekhub.coursework.delivery.authorization.entities.Role;
import org.geekhub.coursework.delivery.authorization.repository.RoleRepository;
import org.geekhub.coursework.delivery.authorization.repository.UserRepository;
import org.geekhub.coursework.delivery.authorization.exceptions.FieldValidationException;
import org.geekhub.coursework.delivery.authorization.exceptions.ForgotResetPassException;
import org.geekhub.coursework.delivery.authorization.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final RoleRepository roleRepository;

    private static final Long ROLE_USER = 1L;
    private static final Long ROLE_ADMIN = 2L;

    public UserService(UserRepository userRepository, EmailService emailService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public Long findUserIdByUsername(String username) {
        return userRepository.findByUsername(username).getId();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isEmailUnique(String email) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            throw new UserException("Can't find user with email: " + email);
        }

        return true;
    }

    public boolean isUsernameUnique(String username) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            throw new UserException("Can't find user with username: " + username);
        }

        return true;
    }

    public boolean saveUser(User user) {
        User userFromDB = userRepository.findByEmail(user.getEmail());
        if (userFromDB != null) {
            throw new UserException("User is not found");
        }

        user.setUsername(user.getUsername());
        user.setEmail(user.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        userRepository.save(user);

        return true;
    }

    public boolean authenticateUser(String email, String password) {
        User user = findByEmail(email);
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new FieldValidationException("Passwords do not match");
        }

        return true;
    }

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return findUserIdByUsername(currentPrincipalName);
    }

    public void changeUserRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("User is not found"));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new UserException("Role of the user is not found"));
        user.getRoles().clear();
        user.getRoles().add(role);

        userRepository.save(user);
    }

    public List<User> getAllUsersWithRoleUser() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ROLE_USER));

        return userRepository.findAllByRoles(roles);
    }

    public List<User> getAllUsersWithAdminAndUserRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ROLE_USER));
        roles.add(new Role(ROLE_ADMIN));

        return userRepository.findAllByRoles(roles);
    }

    public String forgotPass(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ForgotResetPassException("Invalid email");
        }

        String token = UtilsToken.generateToken();
        user.setToken(token);
        user.setTokenCreationDate(LocalDateTime.now());

        userRepository.save(user);

        String subject = "Reset Password";
        String text = "To reset your password, please click on link: https://localhost:8889/reset-password?token=" + token;
        emailService.sendSimpleMessage(email, subject, text);

        return "Please check your email for password reset instructions.";
    }

    public void resetPass(String token, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new FieldValidationException("Passwords do not match");
        }

        User user = userRepository.findByToken(token);
        if (user == null) {
            throw new ForgotResetPassException("Invalid email");
        }

        LocalDateTime tokenCreationDate = user.getTokenCreationDate();

        if (UtilsToken.isTokenExpired(tokenCreationDate)) {
            throw new ForgotResetPassException("Token expired");
        }

        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setToken(null);
        user.setTokenCreationDate(null);

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserException("Can't find user with email: " + email);
        }

        return user;
    }
}
