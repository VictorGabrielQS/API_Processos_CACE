package cace.processos_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.reset-secret}")
    private String resetPasswordSecret;

    @Value("${jwt.reset-expiration}")
    private long resetExpiration;  // Exemplo: 1800000 (30 minutos em ms)


    // --- Métodos para token normal ---

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Pode adicionar claims extras aqui se quiser
        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secret) {
        final Claims claims = extractAllClaims(token, secret);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, String secret) {
        Key key = getSignInKey(secret);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, secretKey);
    }

    private boolean isTokenExpired(String token, String secret) {
        return extractExpiration(token, secret).before(new Date());
    }

    private Date extractExpiration(String token, String secret) {
        return extractClaim(token, Claims::getExpiration, secret);
    }



    // --- Métodos para token de reset de senha ---

    public String generateResetToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + resetExpiration))  // tempo só do reset
                .signWith(getSignInKey(resetPasswordSecret), SignatureAlgorithm.HS256)  // chave do reset
                .compact();
    }

    public String extractResetUsername(String token) {
        return extractClaim(token, Claims::getSubject, resetPasswordSecret);
    }

    public boolean isResetTokenValid(String token, UserDetails userDetails) {
        final String username = extractResetUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, resetPasswordSecret);
    }

}
