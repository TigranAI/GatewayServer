package ru.tigran.gatewayproxy.exception;

public class JwtExpiredException extends JwtException{
    public JwtExpiredException() {
        super("Expired JWT token");
    }
}
