package com.juan.transactionservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        List<String> roles = (List<String>) claims.get("roles");

        return roles.stream()
                .map(role->"ROLE_"+role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
