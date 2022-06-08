package ru.tigran.gatewayproxy.filters;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserFilter extends BaseFilter {
    @Override
    public List<String> getRequiredRoles() {
        return List.of("USER");
    }
}
