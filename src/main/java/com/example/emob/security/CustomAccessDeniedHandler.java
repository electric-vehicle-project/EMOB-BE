/* EMOB-2025 */
package com.example.emob.security;

import com.example.emob.constant.ErrorCode;
import com.example.emob.model.response.APIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json");

    APIResponse<?> body =
        APIResponse.error(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage());

    response.getWriter().write(objectMapper.writeValueAsString(body));
  }
}
