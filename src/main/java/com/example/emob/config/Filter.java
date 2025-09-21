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
    Account account;

    @Autowired
    TokenService tokenService;

    @Autowired
    @Resource(name = "handlerExceptionResolver")
    HandlerExceptionResolver resolver;

    // danh sách uri
    @Value("${security.public-apis}")
    private List<String> authPermission;

    // kiểm tra uri có tồn tại trong List k
    // nếu có thì cho truy cập kh cần token
    // còn kh thì chưa có token
    private boolean checkIsPublicAPI (String uri) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return authPermission.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean isPublicAPI = checkIsPublicAPI(request.getRequestURI());
        if (isPublicAPI) {
            // nếu true thì cho phép truy cập
            filterChain.doFilter(request, response);
        } else {
            // nếu false thì phải kiểm tra token
            String token = getToken(request);
            // nếu có token thì authenticate => lấy thông tin Account từ token
            if (token != null) {
                try {
                    account = tokenService.verifyToken(token);
                } catch (ExpiredJwtException ex) { // response token hết hạn
                    resolver.resolveException(request, response, null, new GlobalException(ErrorCode.EXPIRED_TOKEN));
                    return;
                } catch (MalformedJwtException ex) { // response token sai
                    resolver.resolveException(request, response, null, new GlobalException(ErrorCode.INVALID_TOKEN));
                    return;
                } catch (SignatureException ex) { // response token không khớp với secret_key
                    resolver.resolveException(request, response, null, new GlobalException(ErrorCode.NOT_MATCH_TOKEN));
                    return;
                }
                /*
                    => token chuẩn
                    => cho phép truy cập
                    => lưu thông tin account vào SecurityContext và cho request đi tiếp
                 */
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // tạo context
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    securityContext.setAuthentication(auth);
                    SecurityContextHolder.setContext(securityContext);
                }
                // token oke và cho vào
                filterChain.doFilter(request, response);
            } else {
                resolver.resolveException(request, response, null, new GlobalException(ErrorCode.EMPTY_TOKEN));
            }
        }
    }

    // method: lấy token từ request
    public String getToken (HttpServletRequest request) {
        // token lưu trong Authorization
        String authHeader = request.getHeader("Authorization");
        // nếu có token thì cắt chuỗi sau Bearer để lấy token
        return authHeader == null ? null : authHeader.substring(7).trim();
    }
}
