package ru.tigran.gatewayproxy.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.tigran.gatewayproxy.exception.JwtException;
import ru.tigran.gatewayproxy.exception.JwtExpiredException;
import ru.tigran.gatewayproxy.jwt.JwtClaims;
import ru.tigran.gatewayproxy.jwt.JwtProperties;
import ru.tigran.gatewayproxy.jwt.JwtToken;
import ru.tigran.gatewayproxy.util.ServerHttpResponseBuilder;

import java.net.URI;

@Component
public abstract class BaseFilter implements GatewayFilter, IRequireRoles {
    @Autowired
    JwtProperties properties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(exchange.getRequest().getURI());
            System.out.println(objectMapper.writeValueAsString(exchange.getRequest().getHeaders()));
            System.out.println(objectMapper.writeValueAsString(exchange.getResponse().getHeaders()));
            System.out.println();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        URI origin = request.getURI();
        ServerHttpResponseBuilder builder = ServerHttpResponseBuilder.of(exchange.getResponse());

        if (origin.getPath().equals("/auth")
                || origin.getPath().endsWith(".css")
                || origin.getPath().endsWith(".js")
                || origin.getPath().endsWith(".ico"))
            return chain.filter(exchange);

        if (!headers.containsKey(properties.getHeader()))
            return builder.redirect(origin, "auth").complete();

        final String value = headers.get(properties.getHeader()).get(0);

        try {
            JwtToken token = JwtToken.from(value, properties);
            JwtClaims claims = token.getClaims(properties);
            if (hasAccess(claims)) {
                exchange.getRequest().mutate()
                        .header("user", String.valueOf(claims.getUsername()))
                        .build();
            } else return builder.redirect(origin, "404").complete();
        } catch (JwtExpiredException e) {
            return builder.redirect(origin, "refresh").complete();
        } catch (JwtException e) {
            return builder.redirect(origin, "auth").complete();
        }

        return chain.filter(exchange);
    }

    private boolean hasAccess(JwtClaims claims) {
        try {
            return claims.getAuthorities().containsAll(getRequiredRoles());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
