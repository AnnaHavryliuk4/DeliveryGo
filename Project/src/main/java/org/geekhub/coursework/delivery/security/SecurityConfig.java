package org.geekhub.coursework.delivery.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.geekhub.coursework.delivery.authorization.repository.RoleRepository;
import org.geekhub.coursework.delivery.authorization.repository.UserRepository;
import org.geekhub.coursework.delivery.authorization.service.EmailService;
import org.geekhub.coursework.delivery.authorization.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final int SEVEN_DAYS = 604800;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public SecurityConfig(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests(authorize -> authorize
                .requestMatchers("/login", "/registration", "/styles/css/**", "/static/images/restaurants/**", "/forgot-password", "/reset-password").permitAll()
                .requestMatchers("/**").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/admin/**", "/superadmin/**").hasRole("SUPER_ADMIN")
            )
            .formLogin(login -> login
                .loginPage("/login")
                .usernameParameter("email")
                .failureUrl("/login?error=true")
                .successHandler(successHandler()))
            .rememberMe(remember -> remember
                .userDetailsService(userDetailsService())
                .tokenValiditySeconds(SEVEN_DAYS))
            .logout(logout -> logout
                .logoutUrl("logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login"));
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                for (GrantedAuthority authority : authorities) {
                    if (authority.getAuthority().equals("ROLE_ADMIN")) {
                        response.sendRedirect("/admin");
                        return;
                    } else if (authority.getAuthority().equals("ROLE_SUPER_ADMIN")) {
                        response.sendRedirect("/superadmin");
                        return;
                    }
                }
                response.sendRedirect("/");
            }
        };
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService(userRepository, new EmailService(javaMailSender()), roleRepository);
    }

    @Bean
    public DaoAuthenticationProvider authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        authProvider.setUserDetailsService(userDetailsService());
        return authProvider;
    }
}
