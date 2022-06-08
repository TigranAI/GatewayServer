package ru.tigran.gatewayproxy.exception;

public class JwtInvalidSignatureException extends JwtException {
    public JwtInvalidSignatureException() {
        super("Invalid JWT signature");
    }
}