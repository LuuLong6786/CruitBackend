package com.tma.recruit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tma.recruit.util.Constant;
import com.tma.recruit.util.MessageConstants;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Value("${recruit.app.jwtSecret}")
    private String jwtSecret;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), getErrorMessage(request.getHeader(Constant.AUTHENTICATION_HEADER)));
    }

    public String getErrorMessage(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return MessageConstants.UNAUTHORIZED;
        } catch (SignatureException e) {
            return MessageConstants.INVALID_JWT_SIGNATURE;
        } catch (MalformedJwtException e) {
            return MessageConstants.INVALID_JWT;
        } catch (ExpiredJwtException e) {
            return MessageConstants.TOKEN_EXPIRED;
        } catch (UnsupportedJwtException e) {
            return MessageConstants.TOKEN_UNSUPPORTED;
        } catch (IllegalArgumentException e) {
            return MessageConstants.CLAIMS_IS_EMPTY;
        }
    }
}