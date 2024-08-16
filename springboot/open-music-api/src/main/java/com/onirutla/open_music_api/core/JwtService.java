package com.onirutla.open_music_api.core;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final Environment env;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username != null && username.equals(userDetails.getUsername())) && !isExpired(token);
    }

    public Claims extractAllClaimsFromToken(String token) {
        byte[] keyBytes = Decoders.BASE64.decode(env.getProperty("environment.secret_key"));
        Key signingKey = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> extractRolesFromToken(String token) {
        final Claims claims = extractAllClaimsFromToken(token);
        return claims.get("roles", List.class);
    }

    private boolean isExpired(String token) {
        return extractExpiration(token).before(Date.from(Instant.now(Clock.systemUTC())));
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return generateToken(Jwts.claims(claims), userDetails);
    }

    public String generateToken(Claims claims, UserDetails userDetails) {
        Instant instantIssued = Instant.now(Clock.systemUTC());
        Date dateIssued = Date.from(instantIssued);

        Instant instantExpiration = instantIssued.plus(12, ChronoUnit.HOURS);
        Date dateExpiration = Date.from(instantExpiration);

        log.atDebug()
                .addKeyValue("instant_issued", instantIssued)
                .addKeyValue("instant_expiration", instantExpiration)
                .addKeyValue("process", "generateToken")
                .log();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(dateIssued)
                .setExpiration(dateExpiration)
                .compact();
    }
}
