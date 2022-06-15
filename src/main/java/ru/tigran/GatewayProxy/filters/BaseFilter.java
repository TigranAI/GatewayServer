package ru.tigran.gatewayproxy.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.tigran.gatewayproxy.exception.JwtException;
import ru.tigran.gatewayproxy.exception.JwtExpiredException;
import ru.tigran.gatewayproxy.jwt.JwtProperties;
import ru.tigran.gatewayproxy.util.ServerExchangeBuilder;

@Component
@Slf4j
public abstract class BaseFilter implements GatewayFilterFactory<BaseFilter.Config>, IRequireRoles {
    @Autowired
    JwtProperties properties;
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerExchangeBuilder builder = ServerExchangeBuilder.of(exchange);
        if (builder.hasJwtCookie()) {
            if (builder.isAuthPath())
                return chain.filter(builder.mutatePath("/profile").get());
            try {
                builder.parseToken(properties).refreshCookies();
                if (builder.hasAccess(getRequiredRoles())) return chain.filter(builder.get());
                return chain.filter(builder.mutatePath("/404").get());
            } catch (JwtExpiredException e) {
                return chain.filter(builder.mutatePath("/refresh").get());
            } catch (JwtException e) {
                return chain.filter(builder.removeCookies().mutatePath("/auth").get());
            }
        } else if (builder.isResourcePath() || builder.isAuthPath()) {
            return chain.filter(builder.get());
        }
        return chain.filter(builder.removeCookies().mutatePath("/auth").get());
    }

    @Override
    public GatewayFilter apply(Config config) {
        return this::filter;
    }

    @Override
    public Class<Config> getConfigClass() {
        return BaseFilter.Config.class;
    }

    @Override
    public Config newConfig() {
        return new Config(this.getClass().getTypeName());
    }

    public static class Config {

        public Config(String name){
            this.name = name;
        }
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
