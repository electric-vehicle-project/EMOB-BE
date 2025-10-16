/* EMOB-2025 */
package com.example.emob.config;

import com.example.emob.security.CustomAccessDeniedHandler;
import com.example.emob.security.CustomAuthenticationEntryPoint;
import com.example.emob.service.AuthenticationService;

import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired Filter filter;
    @Autowired private AuthenticationService authenticationService;

    @Autowired private CustomAccessDeniedHandler accessDeniedHandler;
    @Autowired private CustomAuthenticationEntryPoint authenticationEntryPoint;

    // Public
    public static final String[] PUBLIC = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/logout",
            "/api/auth/refresh",
            "/api/auth/refresh-token",
            "/api/auth/forgot-password",
            "/api/auth/verify-otp",
            "/api/auth/resend-otp",
            "/api/public/**",
            "/api/delivery/**"
    };

    // ADMIN
    public static final String[] ADMIN = {
        "/api/dealer/**","/api/vehicle-price-rules"
    };

    public static final String[] DEALER_STAFF = {
        "/api/dealer-staff/report/**",
            "/api/dealer-staff/test-drive/**",
            "/api/contract/**",
            "/api/customers/**"
    };

    public static final String[] EVM_STAFF = {
        "/api/vehicle/**",
    };

    public static final String[] MANAGER = {
            "/api/test-drive/schedules/**",
            "/api/report/process-report/**",
    };
    // Authenticated chung
    public static final String[] AUTHENTICATED = {
            "/api/products/**", "/api/cart/**", "/api/files/**", "/api/notifications/**",
            "/api/auth/reset-password", "/api/promotion/**"
    };
    public static final String[] SWAGGER = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml"
    };

    public static final String[] MULTI_ROLE_ACCESS = {
            "/api/promotion/view-all/**",
    };
  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults()) // bật cors ở security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        req ->
                                req.requestMatchers(PUBLIC)
                                        .permitAll()
                                        .requestMatchers(SWAGGER)
                                        .permitAll()
                                        .requestMatchers(DEALER_STAFF)
                                        .hasRole("DEALER_STAFF")
                                        .requestMatchers(EVM_STAFF)
                                        .hasRole("EVM_STAFF")
                                        .requestMatchers(MANAGER)
                                        .hasRole("MANAGER")
                                        .requestMatchers(MULTI_ROLE_ACCESS)
                                        .hasAnyRole("ADMIN", "MANAGER", "EVM_STAFF", "DEALER_STAFF")
                                        .requestMatchers(ADMIN)
                                        .hasRole("ADMIN")
                                        .requestMatchers(AUTHENTICATED)
                                        .authenticated()
                                        .anyRequest()
                                        .denyAll())
                .exceptionHandling(
                        ex ->
                                ex.authenticationEntryPoint(authenticationEntryPoint)
                                        .accessDeniedHandler(accessDeniedHandler))
                .userDetailsService(authenticationService)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}