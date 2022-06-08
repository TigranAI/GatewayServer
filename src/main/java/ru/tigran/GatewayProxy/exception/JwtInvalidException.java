package ru.tigran.gatewayproxy.exception;
public class JwtInvalidException extends JwtException {
    public JwtInvalidException() {
        super("Invalid JWT token");
    }
}