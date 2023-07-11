package com.twb.pokergame.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * This was required to get the websocket to connect from html/js SockJS connection
 * The WebMvcConfigurer did not seem to work however the StompEndpointRegistry.setAllowedOrigins is required
 */
@Component
public class CorsFilter implements Filter {

    @Value("${app.cors.allow-origins}")
    private String allowOrigins;

    @Value("${app.cors.allow-credentials}")
    private boolean allowCredentials;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, allowOrigins);
        httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(allowCredentials));
        chain.doFilter(request, response);
    }
}
