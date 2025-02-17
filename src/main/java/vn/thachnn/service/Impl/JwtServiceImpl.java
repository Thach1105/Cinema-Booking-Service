package vn.thachnn.service.Impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.thachnn.common.TokenType;
import vn.thachnn.exception.InvalidDataException;
import vn.thachnn.service.JwtService;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

import static vn.thachnn.common.TokenType.ACCESS_TOKEN;
import static vn.thachnn.common.TokenType.REFRESH_TOKEN;

@Service
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryMinutes}")
    private long EXPIRY_MINUTES;

    @Value("${jwt.expiryDay}")
    private long EXPIRY_DAY;

    @Value("${jwt.accessKey}")
    private String ACCESS_KEY;

    @Value("${jwt.refreshKey}")
    private String REFRESH_KEY;

    @Override
    public String generateAccessToken(long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        log.info("Generate access token for user {} with authorities {}", userId, authorities);

        Map<String, Object> claim = new HashMap<>();
        claim.put("userId", userId);
        claim.put("role", authorities);

        return generateAccessToken(claim, username);
    }

    @Override
    public String generateRefreshToken(long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        log.info("Generate refresh token for user {} with authorities {}", userId, authorities);

        Map<String, Object> claim = new HashMap<>();
        claim.put("userId", userId);
        claim.put("role", authorities);

        return generateRefreshToken(claim, username);
    }

    @Override
    public String extractUsername(String token, TokenType type) {
        return extractClaim(token, type, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, TokenType type, UserDetails userDetails) {
        final String username = extractUsername(token, type);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, type);
    }

    public String verifyToken(String token, TokenType type) throws Exception {
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(getSingingKey(type))
                .build();

        try {
            jwtParser.parse(token);

        } catch (Exception e) {
            throw new Exception("Could not verify JWT token integrity!", e);
        }

        return "true";
    }

    private Date extractExpiration(String token, TokenType type){
        return extractClaim(token, type, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token, TokenType type){
        return extractExpiration(token, type).before(new Date());
    }

    private <T> T extractClaim(String token, TokenType type, Function<Claims, T> claimsResolvers){
        final Claims claims = extractAllClaims(token, type);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token, TokenType type){
        try {
            log.info("Extract claims from token {} with type {}", token, type);
            return Jwts.parser()
                    .verifyWith(getSingingKey(type))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException | ExpiredJwtException e) {
            log.error("Invalid JWT Token: {}", token);
            throw new AccessDeniedException("Access denied!, error: "+ e.getMessage());
        }
    }

    private String generateAccessToken(Map<String, Object> claims, String username) {
        log.info("Generate access token for user {}", username);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * EXPIRY_MINUTES))
                .signWith(getSingingKey(ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(Map<String, Object> claims, String username) {
        log.info("Generate refresh token for user {}", username);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * EXPIRY_DAY))
                .signWith(getSingingKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String getSecretKey(TokenType type){
        switch (type) {
            case ACCESS_TOKEN -> {
                return ACCESS_KEY;
            }

            case REFRESH_TOKEN -> {
                return REFRESH_KEY;
            }

            default -> throw new InvalidDataException("Invalid token type");
        }
    }

    private SecretKey getSingingKey(TokenType type) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(getSecretKey(type)));
    }


}
