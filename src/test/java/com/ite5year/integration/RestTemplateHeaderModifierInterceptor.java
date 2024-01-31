package com.ite5year.integration;

import com.ite5year.authentication.handlers.JwtUtils;
import com.ite5year.services.ApplicationUserDetailsImpl;
import com.ite5year.services.ApplicationUserDetailsServiceImpl;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

public class RestTemplateHeaderModifierInterceptor implements ClientHttpRequestInterceptor {
    private final ApplicationUserDetailsServiceImpl userDetailsService;
    JwtUtils jwtUtils;

    RestTemplateHeaderModifierInterceptor(ApplicationUserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = new JwtUtils();
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        try {
            ApplicationUserDetailsImpl userDetails = (ApplicationUserDetailsImpl) userDetailsService.loadUserByUsername("hiba@hiba.com");
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            String jwt = jwtUtils.generateJwtToken(authentication);
            request.getHeaders().add("Authorization", "Bearer " + jwt);
            return execution.execute(request, body);
        } catch (IOException e) {
            return null;
        }

    }
}