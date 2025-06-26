package com.sep490.gshop.config.security.jwt;

import com.sep490.gshop.config.security.services.UserDetailsImpl;
import com.sep490.gshop.entity.User;
import com.sep490.gshop.utils.DateTimeUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Log4j2
public class JwtUtils {

  @Value("${global-shopper.jwt.secret}")
  private String jwtSecret;

  @Value("${global-shopper.jwt.expiration-ms}")
  private int jwtExpirationMs;

  private int jwtTempExpirationInMs = 300000;
  public String generateJwtToken(Authentication authentication) {

    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
    Claims claims = Jwts.claims().setSubject(userPrincipal.getUsername());
    claims.put("role", userPrincipal.getRole());
    claims.put("id", userPrincipal.getId());
    claims.put("email", userPrincipal.getEmail());
    claims.put("name", userPrincipal.getName());
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateJwtToken(User user) {
    Claims claims = Jwts.claims().setSubject(user.getEmail());
    claims.put("role", user.getRole());
    claims.put("id", user.getId());
    claims.put("email", user.getEmail());
    claims.put("name", user.getName());
    return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(key(), SignatureAlgorithm.HS256)
            .compact();
  }

  public String generateTempToken(String email) {

    return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtTempExpirationInMs))
            .signWith(key(), SignatureAlgorithm.HS256)
            .compact();
  }


  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build()
               .parseClaimsJws(token).getBody().getSubject();
  }
  public String getEmailFromToken(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
              .setSigningKey(key())
              .build()
              .parseClaimsJws(token)
              .getBody();

      return claims.getSubject();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true;
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
}
