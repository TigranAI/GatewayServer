package ru.tigran.gatewayproxy.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProperties {
    @Value("${jwt.token.header}")
    private String header;
    @Value("${jwt.token.prefix}")
    private String prefix;

    @Value("${jwt.token.secret}")
    private String secret;

    public String getHeader() {
        return header;
    }
    public String getPrefix() {
        return prefix;
    }
    public String getSecret() {
        return secret;
    }
}
