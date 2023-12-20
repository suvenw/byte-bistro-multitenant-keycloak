package com.langworthytech.bytebistro.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

        List<GrantedAuthority> roles = ((List<String>) realmAccess.get("roles")).stream()
                .map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        List<SimpleGrantedAuthority> scopes = Arrays.stream(jwt.getClaimAsString("scope").split(" ")).toList()
                .stream().map(scopeName -> "SCOPE_" + scopeName).map(SimpleGrantedAuthority::new).toList();

        List<GrantedAuthority> authorities = new ArrayList<>(roles);

        authorities.addAll(scopes);
        return authorities;
    }
}
