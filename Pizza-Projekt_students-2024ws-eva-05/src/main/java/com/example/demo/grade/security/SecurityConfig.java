package com.example.demo.grade.security;

import com.example.demo.grade.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.grade.security.CustomAuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class SecurityConfig {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(UserService userService, CustomAuthenticationSuccessHandler successHandler) {
        this.userService = userService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/register", "/login", "/", "/warenkorb", "/h2-console/**","/images/**",
                        		"/add-to-cart/**","/error", "/favicon.png","/css/**",
                        		"/fonts/**","/remove-from-cart","/users/**","/favicon.ico","/js/**","/vendor/**").permitAll() // Доступ без аутентификации
                        .requestMatchers("/admin/users").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )

                .headers(headers -> headers.frameOptions().sameOrigin())
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .exceptionHandling(
                        exception -> exception.authenticationEntryPoint((request, response, authException) -> {
                            logger.info("Unauthorized access attempt to: {}", request.getRequestURI());
                            response.sendRedirect("/login");
                        }));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}