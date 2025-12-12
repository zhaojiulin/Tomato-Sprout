package com.tomato.sprout.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author zhaojiulin
 * @version 1.0
 * @description: 请求头安全设置
 * @date 2025/12/12 11:45
 */
public class SecurityHeadersFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws ServletException, IOException {
        HttpServletResponse response = (HttpServletResponse) res;

        // 防止点击劫持
        response.setHeader("X-Frame-Options", "DENY");

        // XSS保护
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // MIME类型嗅探保护
        response.setHeader("X-Content-Type-Options", "nosniff");

        // 内容安全策略
        response.setHeader("Content-Security-Policy",
                "default-src 'self'; script-src 'self' 'nonce-{RANDOM}'");

        // HSTS
        response.setHeader("Strict-Transport-Security",
                "max-age=31536000; includeSubDomains");

        chain.doFilter(req, response);
    }
}