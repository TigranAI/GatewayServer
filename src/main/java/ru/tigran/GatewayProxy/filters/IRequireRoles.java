package ru.tigran.gatewayproxy.filters;

import java.util.List;

public interface IRequireRoles {
    List<String> getRequiredRoles();
}
