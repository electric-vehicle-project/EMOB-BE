package com.example.emob.config;

import com.example.emob.constant.ErrorCode;
import com.example.emob.entity.Account;
import com.example.emob.exception.GlobalException;
import com.example.emob.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getToken(request);

        if (token != null) {
            try {
                Account account = tokenService.verifyToken(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException e) {
                resolver.resolveException(request, response, null,
                        new GlobalException(ErrorCode.EXPIRED_TOKEN));
                return;
            } catch (MalformedJwtException e) {
                resolver.resolveException(request, response, null,
                        new GlobalException(ErrorCode.INVALID_TOKEN));
                return;
            } catch (SignatureException e) {
                resolver.resolveException(request, response, null,
                        new GlobalException(ErrorCode.NOT_MATCH_TOKEN));
                return;
            } catch (Exception e) {
                resolver.resolveException(request, response, null,
                        new GlobalException(ErrorCode.EMPTY_TOKEN));
                return;
            }
        }

        // Nếu token null và request public → vẫn pass
        filterChain.doFilter(request, response);
    }



    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.substring(7);
    }
}
