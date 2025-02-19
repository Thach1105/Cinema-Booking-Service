package vn.thachnn.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.thachnn.common.TokenType;

import java.util.Collection;
import java.util.List;

public interface JwtService {

    String generateAccessToken (long userId, String username, Collection<? extends GrantedAuthority> authorities);

    String generateRefreshToken(long userId, String username, Collection<? extends GrantedAuthority> authorities);

    String extractUsername(String token, TokenType type);

    List<String> extractRole(String token, TokenType type);

    boolean isTokenValid(String token, TokenType type, UserDetails userDetails);
}
