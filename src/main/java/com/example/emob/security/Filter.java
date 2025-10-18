/* EMOB-2025 */
package com.example.emob.security;

import com.example.emob.config.SecurityConfig;
import com.example.emob.constant.AccountStatus;
import com.example.emob.constant.ErrorCode;
import com.example.emob.entity.Account;
import com.example.emob.exception.GlobalException;
import com.example.emob.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class Filter extends OncePerRequestFilter {
  @Autowired TokenService tokenService;

  @Autowired
  @Qualifier("handlerExceptionResolver")
  HandlerExceptionResolver resolver;

  private boolean isPublicEndpoint(String uri) {
    // Lấy mảng PUBLIC từ SecurityConfig
    String[] publicEndpoints = SecurityConfig.PUBLIC;

    AntPathMatcher pathMatcher = new AntPathMatcher();
    return Arrays.stream(publicEndpoints).anyMatch(pattern -> pathMatcher.match(pattern, uri));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String path = request.getRequestURI();
    String token = getToken(request);
    if (isPublicEndpoint(path)) {
      filterChain.doFilter(request, response);
      return;
    }


    if (token != null) {
      try {

        Account account = tokenService.verifyToken(token);
        if(account.getStatus().equals(AccountStatus.BANNED)){
            resolver.resolveException(
                request, response, null, new GlobalException(ErrorCode.ACCOUNT_BANNED));
            return;
        }
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println(
            "Authorities: "
                + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
      } catch (ExpiredJwtException e) {
        resolver.resolveException(
            request, response, null, new GlobalException(ErrorCode.EXPIRED_TOKEN));
        return;
      } catch (MalformedJwtException e) {
        resolver.resolveException(
            request, response, null, new GlobalException(ErrorCode.INVALID_TOKEN));
        return;
      } catch (SignatureException e) {
        resolver.resolveException(
            request, response, null, new GlobalException(ErrorCode.NOT_MATCH_TOKEN));
        return;
      } catch (JwtException e) {
        resolver.resolveException(
            request, response, null, new GlobalException(ErrorCode.INVALID_TOKEN));
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private String getToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }
}
