package com.qburst.blog_application.security;

import com.qburst.blog_application.exception.ApiError;
import com.qburst.blog_application.exception.auth.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String message = "Authentication failed";

        if (authException instanceof JwtAuthenticationException jwtEx) {

            switch (jwtEx.getErrorType()) {
                case EXPIRED -> message = "JWT expired";
                case MALFORMED -> message = "Invalid token format";
                case UNSUPPORTED -> message = "JWT unsupported";
                case SIGNATURE_INVALID -> message = "JWT signature invalid";
                case EMPTY -> message = "JWT token missing";
                case BLACKLISTED -> message = "Token is blacklisted";
            }
        } else if (authException instanceof InsufficientAuthenticationException) {
            message = "Full authentication is required to access this resources...";
        }

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .error("Unauthorized")
                .message(message)
//                .message(authException.getMessage())
                .path(request.getRequestURI())
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(error)
        );
    }
}
