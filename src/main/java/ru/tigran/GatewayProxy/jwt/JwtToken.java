package ru.tigran.gatewayproxy.jwt;

import io.jsonwebtoken.*;
import ru.tigran.gatewayproxy.exception.*;
import ru.tigran.gatewayproxy.exception.JwtException;

public class JwtToken {
    private final String value;

    private JwtToken(String value) {
        this.value = value;
    }

    public static JwtToken from(String value, JwtProperties properties) {
        return new JwtToken(value.replace(properties.getPrefix(), ""));
    }

    public String get() {
        return value;
    }

    public JwtClaims getClaims(JwtProperties properties) throws JwtException {
        try {
            Claims claims = Jwts.parser().setSigningKey(properties.getSecret()).parseClaimsJws(value).getBody();
            return JwtClaims.of(claims);
        } catch (SignatureException ex) {
            throw new JwtInvalidSignatureException();
        } catch (MalformedJwtException ex) {
            throw new JwtInvalidException();
        } catch (ExpiredJwtException ex) {
            throw new JwtExpiredException();
        } catch (UnsupportedJwtException ex) {
            throw new JwtUnsupportedException();
        } catch (IllegalArgumentException ex) {
            throw new JwtTokenMissingException();
        }
    }

    public String withPrefix(JwtProperties properties) {
        return String.format("%s%s", properties.getPrefix(), value);
    }
}