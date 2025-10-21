/* EMOB-2025 */
package com.example.emob.config;

import com.example.emob.security.CustomAccessDeniedHandler;
import com.example.emob.security.CustomAuthenticationEntryPoint;
import com.example.emob.security.Filter;
import com.example.emob.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    "/api/auth/logout",
    "/api/auth/refresh",
    "/api/auth/refresh-token",
    "/api/auth/forgot-password",
    "/api/auth/verify-otp",
    "/api/auth/resend-otp",
    "/api/public/**",
    "/api/delivery/**",
    "/api/installment/**"
  };

  // ADMIN
  public static final String[] ADMIN = {
    "/api/dealer/**",
    "/api/vehicle-price-rules",
    "/api/vehicle/{id}/prices",
    "/api/auth/register-by-admin",
    "api/auth/by-admin",
    "/api/dealer-discount-policy/**",
    "/api/vehicle-price-rules/**"
  };

  public static final String[] DEALER_STAFF = {
    "/api/report/**",
    "/api/test-drive/**",
    "/api/contract/**",
    "/api/customers/**",
    "/api/quotation/**",
  };

  public static final String[] EVM_STAFF = {
    "/api/vehicle/**",
  };

  public static final String[] MANAGER = {
    "/api/test-drive/schedules/**",
    "/api/report/process-report/**",
    "api/auth/by-manager",
    "/api/auth/register-by-manager",
  };
  // Authenticated chung
  public static final String[] AUTHENTICATED = {
    "/api/notifications/**", "/api/promotion/**","/api/auth/reset-password"
  };
  public static final String[] SWAGGER = {
    "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml"
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
                    .requestMatchers(HttpMethod.GET, "/**")
                    .authenticated()
                    .requestMatchers(AUTHENTICATED)
                    .authenticated()
                        .requestMatchers(ADMIN)
                        .hasRole("ADMIN")
                        .requestMatchers(EVM_STAFF)
                        .hasRole("EVM_STAFF")
                        .requestMatchers(MANAGER)
                        .hasRole("MANAGER")
                    .requestMatchers(DEALER_STAFF)
                    .hasRole("DEALER_STAFF")
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
