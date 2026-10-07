package com.flickpay.wallets.infrastructure.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PodIdentityResponseFilter extends OncePerRequestFilter {
    private static final String POD_NAME_HEADER = "X-Pod-Name";

    private final String podName;

    public PodIdentityResponseFilter(@Value("${APP_POD_NAME:local}") String podName) {
        this.podName = podName;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        response.setHeader(POD_NAME_HEADER, podName);
        filterChain.doFilter(request, response);
    }
}
