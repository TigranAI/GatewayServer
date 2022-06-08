package ru.tigran.gatewayproxy.exception;

public class JwtTokenMissingException extends JwtException {
    public JwtTokenMissingException() {
        super("JWT claims string is empty.");
    }
}