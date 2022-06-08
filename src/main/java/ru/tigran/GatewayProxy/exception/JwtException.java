package ru.tigran.gatewayproxy.exception;

public class JwtException extends Exception {
    private String message;
    public JwtException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}