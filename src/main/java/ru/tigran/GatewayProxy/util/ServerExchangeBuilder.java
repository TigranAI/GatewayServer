package ru.tigran.gatewayproxy.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import ru.tigran.gatewayproxy.exception.JwtException;
import ru.tigran.gatewayproxy.jwt.JwtClaims;
import ru.tigran.gatewayproxy.jwt.JwtProperties;
import ru.tigran.gatewayproxy.jwt.JwtToken;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class ServerExchangeBuilder {
    private final String[] resourceExtensions = new String[]{".css", ".js", ".ico"};
    private final String[] authPaths = new String[]{"/auth", "/authorization/login", "/authorization/register", "/authorization/refresh"};
    private ServerWebExchange exchange;
    private JwtClaims claims;

    private ServerExchangeBuilder() {
    }

    public static ServerExchangeBuilder of(ServerWebExchange exchange) {
        ServerExchangeBuilder builder = new ServerExchangeBuilder();
        builder.exchange = exchange;
        return builder;
    }

    public ServerWebExchange get() {
        return exchange;
    }

    public ServerExchangeBuilder parseToken(JwtProperties properties) throws JwtException {
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("jwt");
        JwtToken token = JwtToken.from(cookie.getValue(), properties);
        claims = token.getClaims(properties);
        return this;
    }

    public ServerExchangeBuilder mutatePath(String path) {
        exchange = exchange.mutate().request(exchange.getRequest().mutate().path(path).build()).build();
        return this;
    }

    public ServerExchangeBuilder refreshCookies() throws JwtException {
        return addCookie("user", claims.getUsername())
                .addCookie("jrt", claims.getRefreshToken())
                .addCookie("ath", claims.getAuthoritiesString());
    }

    public ServerExchangeBuilder removeCookies() {
        return removeCookie("user")
                .removeCookie("jrt")
                .removeCookie("ath");
    }

    public ServerExchangeBuilder addCookie(String key, String value) {
        ServerHttpRequest request = exchange.getRequest();
        if (request.getCookies().containsKey(key)) return this;
        exchange.getResponse().addCookie(ResponseCookie.from(key, value).build());
        String cookieHeader = exchange.getRequest().getHeaders().get(HttpHeaders.COOKIE).get(0);
        exchange = exchange.mutate().request(
                exchange.getRequest().mutate()
                        .header(HttpHeaders.COOKIE, String.format("%s; %s=%s", cookieHeader, key, value))
                        .build()).build();
        return this;
    }

    public ServerExchangeBuilder removeCookie(String key) {
        exchange.getResponse().addCookie(ResponseCookie.from(key, "").maxAge(0).build());
        return this;
    }

    public boolean hasAccess(List<String> requiredRoles) throws JwtException {
        return claims.getAuthorities().containsAll(requiredRoles);
    }

    public boolean hasJwtCookie() {
        return exchange.getRequest().getCookies().containsKey("jwt");
    }

    public boolean isResourcePath() {
        String path = exchange.getRequest().getURI().getPath();
        return Arrays.stream(resourceExtensions).anyMatch(path::endsWith);
    }

    public boolean isAuthPath() {
        String path = exchange.getRequest().getURI().getPath();
        return Arrays.asList(authPaths).contains(path);
    }
}
