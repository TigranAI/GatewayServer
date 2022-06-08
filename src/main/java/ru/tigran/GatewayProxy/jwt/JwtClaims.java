package ru.tigran.gatewayproxy.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;

import java.util.HashSet;
import java.util.List;

public class JwtClaims {
    Claims claims;

    private JwtClaims(Claims claims) {
        this.claims = claims;
    }

    public static JwtClaims of (Claims claims) {
        return new JwtClaims(claims);
    }

    public String getUsername(){
        return (String) claims.get("usr");
    }

    public String getRefreshToken(){
        return (String) claims.get("jrt");
    }

    public HashSet<String> getAuthorities() throws JsonProcessingException {
        String ath = (String) claims.get("ath");
        ObjectMapper mapper = new ObjectMapper();
        return new HashSet<>(List.of(mapper.readValue(ath, String[].class)));
    }
}
