package ru.tigran.gatewayproxy.jwt;

import io.jsonwebtoken.Claims;

import java.util.ArrayList;
import java.util.HashSet;

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
    public HashSet<String> getAuthorities() {
        return new HashSet<>((ArrayList<String>) claims.get("ath"));
    }
    public String getAuthoritiesString() {
        return String.join(".", ((ArrayList<String>) claims.get("ath")));
    }
}
