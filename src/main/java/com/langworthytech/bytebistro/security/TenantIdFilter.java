package com.langworthytech.bytebistro.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantIdFilter.class);

    private JwtDecoder jwtDecoder;

    public TenantIdFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("doFilterInternal called!");

        String token = request.getHeader("Authorization").substring(7);
        log.info("Authorization Token: {}", token);
        Jwt jwt = jwtDecoder.decode(token);
        String issuer = jwt.getIssuer().toString();
        log.info("Issuer: {}", issuer);

        filterChain.doFilter(request, response);
    }

}
