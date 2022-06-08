package ru.tigran.gatewayproxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.tigran.gatewayproxy.filters.AdminFilter;
import ru.tigran.gatewayproxy.filters.UserFilter;

@Component
public class GatewayConfig {
    @Autowired
    UserFilter userFilter;
    @Autowired
    AdminFilter adminFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("eureka-server", r -> r
                        .path("/admin/**")
                        .filters(f -> f.filter(adminFilter))
                        .uri("http://localhost:4005"))
                .route("eureka", r -> r
                        .path("/eureka")
                        .filters(f -> f.filter(adminFilter)
                                .rewritePath("^/eureka\\/?(?<segment>.*)", "/${segment}"))
                        .uri("http://localhost:8761"))
                .route("eureka-resources", r -> r
                        .path("/eureka/**")
                        .filters(f -> f.filter(adminFilter))
                        .uri("http://localhost:8761"))
                .route("config-server", r -> r
                        .path("/config/**")
                        .uri("http://localhost:8888")
                )
                .route("plagiarism-calc", r -> r
                        .path("/plagiarism/**")
                        .uri("http://localhost:4001")
                )
                .route("auth-service", r -> r
                        .path("/authorize/**")
                        .uri("http://localhost:4003")
                )
                .route("web-editor", r -> r
                        .path("/editor/**")
                        .uri("http://localhost:4004")
                )
                .route("login", r -> r
                        .method("GET")
                        .and()
                        .path("/auth")
                        .uri("http://localhost:4002")
                )
                .route("main-page", r -> r
                        .path("")
                        .uri("http://localhost:4002")
                )
                .route("resource-server", r -> r
                        .path("/**")
                        .filters(f -> f.filter(userFilter))
                        .uri("http://localhost:4002")
                )
                .build();
    }
}
