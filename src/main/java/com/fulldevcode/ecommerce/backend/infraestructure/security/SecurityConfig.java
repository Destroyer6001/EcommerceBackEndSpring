package com.fulldevcode.ecommerce.backend.infraestructure.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;


    public SecurityConfig(JwtAuthFilter jwtFilterAuth)
    {
        this.jwtAuthFilter = jwtFilterAuth;
    }

    @Bean
    public AccessDeniedHandler accessDenied() {
        return  ((request, response, accessDeniedException) -> {
           response.setStatus(HttpServletResponse.SC_FORBIDDEN);
           response.setContentType("application/json");
           response.getWriter().write("{\"error\": \"Acceso denegado: rol insuficiente\"}");
        });
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntry() {
        return ((request, response, authException) -> {
           response.setStatus((HttpServletResponse.SC_UNAUTHORIZED));
           response.setContentType("application/json");
           response.getWriter().write("{\"error\": \"No autenticado: se requiere token JWT válido\"}");
        });
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationEntryPoint authenticationEntry, AccessDeniedHandler accessDenied) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // public routes
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/api/users/register").permitAll()

                        // private routes
                        .requestMatchers("/api/users/editUser/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/api/users/userInfo/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/api/products/getAll").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/api/orders/orderById/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/apo/orders/getOrdersUser/*").hasAnyAuthority("ROLE_USER")
                        .requestMatchers("/api/orders/orderCreate").hasAuthority("ROLE_USER")
                        .requestMatchers("/api/inventoryAdjustments/getAll/*").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/categories/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/orders/getAllOrders").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/inventoryAdjustments/**").hasAnyAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> {
                    ex.authenticationEntryPoint(authenticationEntry);
                    ex.accessDeniedHandler(accessDenied);
                })
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return  http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws  Exception {
        return config.getAuthenticationManager();
    }
}
