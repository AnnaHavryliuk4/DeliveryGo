package org.geekhub.coursework.delivery.authorization.service;

import org.geekhub.coursework.delivery.authorization.entities.Role;
import org.geekhub.coursework.delivery.authorization.entities.User;
import org.geekhub.coursework.delivery.authorization.exceptions.FieldValidationException;
import org.geekhub.coursework.delivery.authorization.exceptions.ForgotResetPassException;
import org.geekhub.coursework.delivery.authorization.exceptions.UserException;
import org.geekhub.coursework.delivery.authorization.repository.RoleRepository;
import org.geekhub.coursework.delivery.authorization.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findUserIdByUsername_shouldReturnUserId() {
        String username = "testUser";
        Long expectedUserId = 123L;
        User user = new User();
        user.setId(expectedUserId);
        when(userRepository.findByUsername(username)).thenReturn(user);

        Long actualUserId = userService.findUserIdByUsername(username);
        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    void findByEmail_ShouldReturnUserByEmail() {
        String email = "test@gmail.com";
        User expectedUser = new User();
        expectedUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(expectedUser);

        User actualUser = userService.findByEmail(email);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void isEmailUnique_whenEmailIsUnique_shouldReturnTrue() {
        String uniqueEmail = "test@example.com";
        when(userRepository.findByEmail(uniqueEmail)).thenReturn(null);

        boolean isUnique = userService.isEmailUnique(uniqueEmail);
        assertTrue(isUnique);
    }

    @Test
    void isEmailUnique_whenEmailIsNotUnique_shouldReturnUserException() {
        String nonUniqueEmail = "test@example.com";
        when(userRepository.findByEmail(nonUniqueEmail)).thenReturn(new User());

        UserException actualException = assertThrows(UserException.class, () -> userService.isEmailUnique(nonUniqueEmail));
        assertEquals("Can't find user with email: " + nonUniqueEmail, actualException.getMessage());
    }

    @Test
    void isUsernameUnique_whenUsernameIsUnique_shouldReturnTrue() {
        String uniqueUsername = "username";
        when(userRepository.findByUsername(uniqueUsername)).thenReturn(null);

        boolean isUnique = userService.isUsernameUnique(uniqueUsername);
        assertTrue(isUnique);
    }

    @Test
    void isEmailUnique_whenUsernameIsNotUnique_shouldReturnUserException() {
        String nonUniqueUsername = "username";
        when(userRepository.findByUsername(nonUniqueUsername)).thenReturn(new User());

        UserException actualException = assertThrows(UserException.class, () -> userService.isUsernameUnique(nonUniqueUsername));
        assertEquals("Can't find user with username: " + nonUniqueUsername, actualException.getMessage());
    }


    @Test
    void saveUser_whenUserNotExist_shouldReturnTrue() {
        User user = new User();
        String email="newuser@example.com";
        String password = "password";
        user.setEmail(email);
        user.setPassword(password);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        boolean saved = userService.saveUser(user);
        assertTrue(saved);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void saveUser_whenUserIsExist_shouldReturnUserException() {
        User user = new User();
        String email="newuser@example.com";
        user.setEmail(email);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPassword");

        UserException actualException = assertThrows(UserException.class, () -> userService.saveUser(user));
        assertEquals("User is not found", actualException.getMessage());
    }

    @Test
    void authenticateUser_whenPasswordsMatch_shouldReturnTrue(){
        User user = new User();
        String email="newuser@example.com";
        String password = "password";
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(bCryptPasswordEncoder.matches(password, user.getPassword())).thenReturn(true);

        boolean authenticated = userService.authenticateUser(email,user.getPassword());
        assertTrue(authenticated);
    }

    @Test
    void authenticateUser_whenPasswordsAreNotMatch_shouldReturnFieldValidationException(){
        User user = new User();
        String email="newuser@example.com";
        String password = "password";
        String hashedPassword = "hashedPassword";
        user.setEmail(email);
        user.setPassword(hashedPassword);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(bCryptPasswordEncoder.matches(password, hashedPassword)).thenThrow(new FieldValidationException("Passwords do not match"));

        FieldValidationException actualException = assertThrows(FieldValidationException.class, () -> userService.authenticateUser(email,password));
        assertEquals("Passwords do not match", actualException.getMessage());
    }

    @Test
    void getUserId_shouldReturnUserIdFromCurrentPrincipal() {
        User user = new User();
        String currentPrincipalName = "testUser";
        Long expectedUserId = 1L;
        user.setUsername(currentPrincipalName);
        user.setId(expectedUserId);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Authentication authentication = new UsernamePasswordAuthenticationToken(currentPrincipalName,user);
        securityContext.setAuthentication(authentication);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByUsername(currentPrincipalName)).thenReturn(user);

        Long actualUserId = userService.getUserId();
        System.out.println(actualUserId);
        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    void changeUserRole_whenUserIsNotFound() {
        Long userId = 1L;
        Long roleId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> userService.changeUserRole(userId, roleId));
        assertEquals("User is not found", exception.getMessage());
    }

    @Test
    void changeUserRole_whenUserRoleIsNotFound() {
        Long userId = 1L;
        Long roleId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> userService.changeUserRole(userId, roleId));
        assertEquals("Role of the user is not found", exception.getMessage());
    }

    @Test
    void changeUserRole_shouldSuccessfullyChangeRole() {
        Long userId = 1L;
        Long roleId = 1L;
        User user = new User();
        Role role = new Role();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        userService.changeUserRole(userId, roleId);

        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, times(1)).findById(roleId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getAllUsersWithRoleUser_shouldReturnAllUsers() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L));

        User user1 = new User();
        user1.setId(1L);
        user1.setRoles(roles);

        User user2 = new User();
        user2.setId(2L);
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(new Role(2L));
        user2.setRoles(adminRoles);

        List<User> allUsers = new ArrayList<>();
        allUsers.add(user1);
        allUsers.add(user2);

        when(userRepository.findAllByRoles(roles)).thenReturn(allUsers);

        List<User> usersWithRoleUser = userService.getAllUsersWithRoleUser();
        assertEquals(1, usersWithRoleUser.size());
    }

    @Test
    void getAllUsersWithAdminAndUserRoles_shouldReturnAllUsersAndAdmins() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L));
        roles.add(new Role(2L));

        User user1 = new User();
        user1.setId(1L);
        user1.setRoles(roles);

        User user2 = new User();
        user2.setId(2L);
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(new Role(2L));
        user2.setRoles(adminRoles);

        User user3 = new User();
        user3.setId(3L);
        Set<Role> superAdminRoles = new HashSet<>();
        adminRoles.add(new Role(3L));
        user3.setRoles(superAdminRoles);

        List<User> allUsers = new ArrayList<>();
        allUsers.add(user1);
        allUsers.add(user2);
        allUsers.add(user3);

        when(userRepository.findAllByRoles(roles)).thenReturn(allUsers);

        List<User> usersWithRoleUser = userService.getAllUsersWithAdminAndUserRoles();
        assertEquals(2, usersWithRoleUser.size());
    }

    @Test
    void forgotPass_ShouldSendEmailAndReturnSuccessMessage() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        String actualResult = userService.forgotPass(email);

        assertEquals("Check your email for password reset instructions", actualResult);

        verify(userRepository, times(1)).save(user);
        verify(emailService, times(1)).sendSimpleMessage(eq(email), anyString(), anyString());
    }

    @Test
    void forgotPass_whenInvalidEmail_shouldReturnForgotResetPassException() {
        String email = "tesr@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        ForgotResetPassException actualException = assertThrows(ForgotResetPassException.class, () -> userService.forgotPass(email));
        assertEquals("Invalid email", actualException.getMessage());
    }

    @Test
    void resetPass_ShouldSendEmailAndReturnSuccessMessage() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        String actualResult = userService.forgotPass(email);
        assertEquals("Check your email for password reset instructions", actualResult);

        verify(userRepository, times(1)).save(user);
        verify(emailService, times(1)).sendSimpleMessage(eq(email), anyString(), anyString());
    }

    @Test
    void resetPass_whenPasswordDoNotMath_shouldReturnForgotResetPassException() {
        String token = "token";
        String password = "password1";
        String confirmPassword = "password2";

        FieldValidationException actualException = assertThrows(FieldValidationException.class,
            () -> userService.resetPass(token,password,confirmPassword));
        assertEquals("Passwords do not match", actualException.getMessage());

    }

    @Test
    void resetPass_whenInvalidToken_shouldReturnForgotResetPassException() {
        String token = "invalid_token";
        when(userRepository.findByToken(token)).thenReturn(null);

        String password = "password";
        String confirmPassword = "password";

        ForgotResetPassException actualException = assertThrows(ForgotResetPassException.class,
            () -> userService.resetPass(token,password,confirmPassword));
        assertEquals("Invalid email", actualException.getMessage());

    }

    @Test
    void resetPass_whenTokenExpired_shouldReturnForgotResetPassException() {
        String token = "invalid_token";
        User user = new User();
        user.setTokenCreationDate(LocalDateTime.now().minusHours(2));
        when(userRepository.findByToken(token)).thenReturn(user);

        String password = "password";
        String confirmPassword = "password";

        ForgotResetPassException actualException = assertThrows(ForgotResetPassException.class,
            () -> userService.resetPass(token,password,confirmPassword));
        assertEquals("Token expired", actualException.getMessage());

    }

    @Test
    void resetPass_ValidTokenAndMatchingPasswords_ShouldResetPassword(){
        String token = "valid_token";
        User user = new User();
        user.setTokenCreationDate(LocalDateTime.now());
        when(userRepository.findByToken(token)).thenReturn(user);

        String password = "password";
        String confirmPassword = "password";
        String encodedPassword = "encodedPassword";
        when(bCryptPasswordEncoder.encode(password)).thenReturn(encodedPassword);

        userService.resetPass(token, password, confirmPassword);
        verify(userRepository, times(1)).save(user);
    }
}
