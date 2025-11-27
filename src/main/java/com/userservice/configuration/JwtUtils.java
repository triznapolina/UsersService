package com.userservice.configuration;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.function.Function;

@Service
@Slf4j
public class JwtUtils {

    @Value("${security.token.signing.key}")
    private String jwtSigningKey;



    public String getUsernameFromJwt(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public String getRolesFromJwt(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }



    public Long getUserIdFromJwt(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("id", Long.class);
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token)
                .getBody();
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }


    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken);
    }

    public boolean validateToken(@NonNull String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey()).build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }

    public Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }



}
