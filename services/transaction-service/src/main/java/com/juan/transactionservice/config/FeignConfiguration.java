package com.juan.transactionservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;



@Configuration
public class FeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                if (requestTemplate.headers().containsKey(HttpHeaders.AUTHORIZATION)) return;

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth instanceof JwtAuthenticationToken jwtAuth && jwtAuth.getToken()!=null) {
                    requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtAuth.getToken().getTokenValue());
                }else if (auth instanceof BearerTokenAuthentication bearerToken && bearerToken.getToken()!=null) {
                    requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken.getToken().getTokenValue());
                }
            }
        };
    }
}
