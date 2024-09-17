package com.onirutla.open_music_api.core;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final Environment env;

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        String secretKey = env.getProperty("environment.access_token_secret_key");
        String encodedSecretKey = Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
        JwtParser jwtParser = getJwtParser(encodedSecretKey);
        Claims claims = getTokenClaims(encodedSecretKey, token);
        String username = claims.getSubject();
        boolean isExpired = claims.getExpiration().before(Date.from(Instant.now(Clock.systemUTC())));
        log.atInfo()
                .addKeyValue("process", "validating_access_token")
                .addKeyValue("access_token", token)
                .addKeyValue("username", username)
                .addKeyValue("claims", claims)
                .addKeyValue("is_expired", isExpired)
                .log("validating access token");
        boolean isValid = username != null && username.equals(userDetails.getUsername()) && jwtParser.isSigned(token) && !isExpired;
        log.atInfo()
                .addKeyValue("process", "validating_access_token")
                .addKeyValue("access_token", token)
                .addKeyValue("username", username)
                .addKeyValue("claims", claims)
                .addKeyValue("is_expired", isExpired)
                .addKeyValue("is_valid", isValid)
                .log("access token validated");
        return isValid;
    }

    private JwtParser getJwtParser(String encodedSecretKey) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(encodedSecretKey.getBytes(StandardCharsets.UTF_8)))
                .build();
    }

    public Claims getTokenClaims(String encodedSecretKey, String token) {
        return getJwtParser(encodedSecretKey).parseSignedClaims(token).getPayload();
    }

    public boolean isRefreshTokenKeyValid(String token, UserDetails userDetails) {
        String secretKey = env.getProperty("environment.refresh_token_secret_key");
        String encodedSecretKey = Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
        JwtParser jwtParser = getJwtParser(encodedSecretKey);

        Claims claims = getTokenClaims(encodedSecretKey, token);
        String username = claims.getSubject();
        boolean isExpired = claims.getExpiration().before(Date.from(Instant.now(Clock.systemUTC())));
        log.atInfo()
                .addKeyValue("process", "validating_refresh_token")
                .addKeyValue("refresh_token", token)
                .addKeyValue("username", username)
                .addKeyValue("claims", claims)
                .addKeyValue("is_expired", isExpired)
                .log("validating refresh token");
        boolean isValid = username != null && username.equals(userDetails.getUsername()) && jwtParser.isSigned(token) && !isExpired;
        log.atInfo()
                .addKeyValue("process", "validating_refresh_token")
                .addKeyValue("refresh_token", token)
                .addKeyValue("username", username)
                .addKeyValue("claims", claims)
                .addKeyValue("is_expired", isExpired)
                .addKeyValue("is_valid", isValid)
                .log("refresh token validated");
        return isValid;
    }


    public String generateAccessToken(Claims claims, UserDetails userDetails) {
        Instant instantIssued = Instant.now(Clock.systemUTC());
        Date dateIssued = Date.from(instantIssued);

        Instant instantExpiration = instantIssued.plus(30, ChronoUnit.MINUTES);
        Date dateExpiration = Date.from(instantExpiration);

        String secretKey = env.getProperty("environment.access_token_secret_key");
        String secretKeyEncoded = Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
        log.atInfo()
                .addKeyValue("process", "generate_token")
                .addKeyValue("instant_issued", instantIssued)
                .addKeyValue("instant_expiration", instantExpiration)
                .addKeyValue("access_token_secret_key", secretKey)
                .addKeyValue("access_token_secret_key_encoded", secretKeyEncoded)
                .addKeyValue("claims", claims)
                .addKeyValue("subject", userDetails.getUsername())
                .setMessage("generating access token")
                .log();
        String jwt = Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(dateIssued)
                .signWith(Keys.hmacShaKeyFor(secretKeyEncoded.getBytes(StandardCharsets.UTF_8)))
                .expiration(dateExpiration)
                .compact();
        log.atInfo()
                .addKeyValue("process", "generate_token")
                .addKeyValue("instant_issued", instantIssued)
                .addKeyValue("instant_expiration", instantExpiration)
                .addKeyValue("access_token_secret_key", secretKey)
                .addKeyValue("access_token_secret_key_encoded", secretKeyEncoded)
                .addKeyValue("claims", claims)
                .addKeyValue("subject", userDetails.getUsername())
                .setMessage("generating access token")
                .log();
        return jwt;
    }

    public String generateRefreshToken(Claims claims, UserDetails userDetails) {
        Instant instantIssued = Instant.now(Clock.systemUTC());
        Date dateIssued = Date.from(instantIssued);

        Instant instantExpiration = instantIssued.plus(2, ChronoUnit.DAYS);
        Date dateExpiration = Date.from(instantExpiration);

        String secretKey = env.getProperty("environment.refresh_token_secret_key");
        String secretKeyEncoded = Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
        log.atInfo()
                .addKeyValue("instant_issued", instantIssued)
                .addKeyValue("instant_expiration", instantExpiration)
                .addKeyValue("refresh_token_secret_key", secretKey)
                .addKeyValue("refresh_token_secret_key_encoded", secretKeyEncoded)
                .addKeyValue("process", "generate_token")
                .setMessage("generating refresh token")
                .log();
        String jwt = Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .signWith(Keys.hmacShaKeyFor(secretKeyEncoded.getBytes(StandardCharsets.UTF_8)))
                .issuedAt(dateIssued)
                .expiration(dateExpiration)
                .compact();
        log.atInfo()
                .addKeyValue("instant_issued", instantIssued)
                .addKeyValue("instant_expiration", instantExpiration)
                .addKeyValue("refresh_token_secret_key", secretKey)
                .addKeyValue("refresh_token_secret_key_encoded", secretKeyEncoded)
                .addKeyValue("process", "generate_token")
                .setMessage("refresh token generated")
                .log();
        return jwt;
    }
}
