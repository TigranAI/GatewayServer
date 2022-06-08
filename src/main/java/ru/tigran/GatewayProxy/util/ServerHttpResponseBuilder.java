package ru.tigran.gatewayproxy.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;
import ru.tigran.gatewayproxy.exception.JwtException;
import ru.tigran.gatewayproxy.jwt.JwtProperties;
import ru.tigran.gatewayproxy.jwt.JwtToken;

import java.net.URI;

public class ServerHttpResponseBuilder {
    private ServerHttpResponse response;

    private ServerHttpResponseBuilder() {
    }

    public static ServerHttpResponseBuilder of(ServerHttpResponse response) {
        ServerHttpResponseBuilder builder = new ServerHttpResponseBuilder();
        builder.response = response;
        return builder;
    }

    public ServerHttpResponseBuilder redirect(URI from, String to) {
        addRedirect(response, getRelativeUri(to, from));
        return this;
    }

    public ServerHttpResponseBuilder authorize(String body, JwtProperties properties) {
        try {
            JwtToken token = JwtToken.from(body, properties);
            response.getHeaders().add(properties.getHeader(), token.withPrefix(properties));
            response.addCookie(ResponseCookie.from("jrt", token.getClaims(properties).getRefreshToken()).build());
        } catch (JwtException e){
            e.printStackTrace();
        }
        return this;
    }

    public Mono<Void> complete() {
        return response.setComplete();
    }

    public ServerHttpResponse get() {
        return response;
    }

    private void addRedirect(ServerHttpResponse response, URI uri) {
        response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        response.getHeaders().setLocation(uri);
    }

    private URI getRelativeUri(String path, URI uri) {
        return URI.create(String.format("%s://%s/%s", uri.getScheme(), uri.getHost(), path));
    }
}
