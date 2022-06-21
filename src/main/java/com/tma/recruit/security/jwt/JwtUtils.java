package com.tma.recruit.security.jwt;

import com.tma.recruit.model.entity.Role;
import com.tma.recruit.model.enums.UserRole;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private static final String ROLE_KEY = "ROLE_";

    private static final String PERMISSION_KEY = "authority";

    @Autowired
    private UserRepository userRepository;

    @Value("${recruit.app.jwtSecret}")
    private String jwtSecret;

    @Value("${recruit.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Date currentDate = new Date();
        String token = Jwts.builder()
                .setId(userPrincipal.getId().toString())
                .setSubject(userPrincipal.getUsername())
//                .claim("authorities", userPrincipal.getAuthorities())
//                .claim(ROLE_KEY, userPrincipal.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .setIssuedAt(currentDate)
                .setExpiration(new Date((currentDate).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
        return token;
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(parseJwtString(token)).getBody().getSubject();
    }

    public List<String> getRoleFromToken(String token) {

        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(parseJwtString(token)).getBody();
        List<String> a= (List<String>) claims.get("authorities");
        return  null;
    }

    public List<String> getPermission(String token) {

        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(parseJwtString(token)).getBody();
        return (List<String>) claims.get(ROLE_KEY);
    }

    public boolean isAdmin(String token) {
        return getRoleFromToken(token).contains(UserRole.ADMIN.toString());
    }

    public boolean isOwner(String token, Long userId) {
        return getUserIdFromJwtToken(token).equals(userId);
    }

    public Long getUserIdFromJwtToken(String token) {
        return Long.parseLong(Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(parseJwtString(token)).getBody().getId());
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public String parseJwtString(String request) {
        if (StringUtils.hasText(request) && request.startsWith("Bearer ")) {
            return request.substring(7);
        }
        return request;
    }
}