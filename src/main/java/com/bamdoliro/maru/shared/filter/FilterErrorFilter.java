package com.bamdoliro.maru.shared.filter;

import com.bamdoliro.maru.domain.auth.exception.ExpiredTokenException;
import com.bamdoliro.maru.domain.auth.exception.InvalidTokenException;
import com.bamdoliro.maru.shared.error.ErrorProperty;
import com.bamdoliro.maru.shared.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class GlobalErrorFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredTokenException | InvalidTokenException e) {
            setErrorResponse(e.getErrorProperty(), response);
        }
    }

    private void setErrorResponse(ErrorProperty errorProperty, HttpServletResponse response) throws IOException {
        response.setStatus(errorProperty.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        new ErrorResponse(errorProperty)
                )
        );
    }
}