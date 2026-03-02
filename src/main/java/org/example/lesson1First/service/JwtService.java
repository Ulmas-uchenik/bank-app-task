package org.example.lesson1First.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.lesson1First.entity.db.TokenEntity;
import org.example.lesson1First.entity.db.UserPassword;
import org.example.lesson1First.enums.TokenType;
import org.example.lesson1First.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${bank.service.auth.secret.key}")
    private String secretKey;
    private final long expiration = 1000*3600*24; // 1000 in 1 second

    private final TokenRepository tokenRepository;

    public String generateToken(UserDetails userDetails) {
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();

        tokenRepository.save(TokenEntity.builder().user((UserPassword) userDetails).tokenType(TokenType.BEARER).token(token).build());
        return token;
    }

//    public String generateRefreshToken(UserDetails userDetails) {
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(getSignInKey())
//                .compact();
//    }

    // ИСПРАВЛЕННЫЙ ВАРИАНТ:
    private Key getSignInKey() {
        // Способ 1: Генерация безопасного ключа автоматически
        byte[] keyBytes = secretKey .getBytes(StandardCharsets.UTF_8);

        // Проверка длины
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("Секретный ключ должен быть минимум 32 символа");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token) {
        log.info("Check token to valid");
        try {
            // 1. Парсинг и проверка подписи (Signature Verification)
            // Если подпись неверна или токен изменен, parseClaimsJws выбросит исключение здесь.
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 2. Проверка срока действия (Expiration)
            boolean isExpired = claims.getExpiration().before(new Date());
            if (isExpired || !isTokenValidInDatabase(token)) throw new JwtException("Ваше токен был отклонен. " + tokenRepository.findByToken(token));
            return true;
        } catch (Exception e) {
            log.error("Token not valid: " + e.getMessage());
            return false;
        }
    }

    public boolean isTokenValidInDatabase(String token){
        return tokenRepository.findByToken(token).map(it -> !it.isExpired() && !it.isRevoked()).orElse(false);
    }

    @Transactional
    public void revokeAllUserTokens(UserPassword user){
        List<TokenEntity> allAvailableUserToken = tokenRepository.findAllAvailableUserToken(user.getEmail());
        if (allAvailableUserToken.isEmpty()) return;
        allAvailableUserToken.forEach(it -> {
            it.setExpired(true);
            it.setRevoked(true);
        });
    }

    @Transactional
    public void revokeToken(String token) {
        tokenRepository.findByToken(token).ifPresent(it -> {
            it.setRevoked(true);
            it.setExpired(true);
        });
    }

    public List<TokenEntity> findAllAvailableUserToken(UserPassword user){
        return tokenRepository.findAllAvailableUserToken(user.getEmail());
    }

    public String extractEmail(String token){
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
