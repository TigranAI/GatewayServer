package ru.tigran.gatewayproxy.exception;

public class JwtUnsupportedException extends JwtException{
    public JwtUnsupportedException() {
        super("Unsupported JWT token");
    }
}
