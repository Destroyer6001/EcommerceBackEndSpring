package com.fulldevcode.ecommerce.backend.infraestructure.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;


    public SecurityConfig(JwtAuthFilter jwtFilterAuth)
    {
        this.jwtAuthFilter = jwtFilterAuth;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:4200", "http://127.0.0.1:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept", "X-Requested-With",
                "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Content-Length", "Access-Control-Allow-Origin"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public AccessDeniedHandler accessDenied() {
        return  ((request, response, accessDeniedException) -> {
           response.setStatus(HttpServletResponse.SC_FORBIDDEN);
           response.setContentType("application/json");
            String json = "{"
                    + "\"success\": false, "
                    + "\"message\": \"Acceso denegado: rol insuficiente\", "
                    + "\"data\": null, "
                    + "\"timestamp\": \"" + LocalDateTime.now() + "\""
                    + "}";
           response.getWriter().write(json);
        });
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntry() {
        return ((request, response, authException) -> {
           response.setStatus((HttpServletResponse.SC_UNAUTHORIZED));
           response.setContentType("application/json");
            String json = "{"
                    + "\"success\": false, "
                    + "\"message\": \"No autenticado: se requiere token JWT válido\", "
                    + "\"data\": null, "
                    + "\"timestamp\": \"" + LocalDateTime.now() + "\""
                    + "}";
           response.getWriter().write(json);
        });
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationEntryPoint authenticationEntry, AccessDeniedHandler accessDenied) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth

                        // public routes
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/api/users/register").permitAll()

                        // private routes

                        // routes for user, admin and deliveries
                        .requestMatchers("/api/users/editUser/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/api/users/userInfo/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/api/products/getAll").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/api/orders/orderById/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/api/orders/cancelOrder/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/api/orders/getOrdersUser/*").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers("/api/categories/getAll").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers("/api/orders/ordersPending").hasAnyAuthority("ROLE_ADMIN", "ROLE_DELIVERY")
                        .requestMatchers("/api/shipments/getAll/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_DELIVERY")
                        .requestMatchers("/api/payslips/getAllPayslipsUser/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_DELIVERY")
                        .requestMatchers("/api/payslips/getById/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_DELIVERY")

                        // routes only user
                        .requestMatchers("/api/orders/orderCreate").hasAuthority("ROLE_USER")

                        // routes only admin
                        .requestMatchers("/api/inventoryAdjustments/getAll/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/categories/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/orders/getAllOrders").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/orders/totalSalesProduct").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/orders/maxSalesProduct").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/orders/ordersForState").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/orders/totalSalesCategory").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/orders/maxSalesProduct").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/inventoryAdjustments/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/shipments/deliveriesUsersTotals").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/payslips/payslipUsersTotal").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/users/getAllDeliveries").hasAuthority("ROLE_ADMIN")

                        // router only delivery
                        .requestMatchers("/api/shipments/*").hasAuthority("ROLE_DELIVERY")
                        .requestMatchers("/api/shipments/cancel/*").hasAuthority("ROLE_DELIVERY")
                        .requestMatchers("/api/shipments/confirm/*").hasAuthority("ROLE_DELIVERY")
                        .requestMatchers("/api/payslips").hasAuthority("ROLE_DELIVERY")
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
