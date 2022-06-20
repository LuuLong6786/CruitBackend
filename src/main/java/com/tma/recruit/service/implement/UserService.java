package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.TokenEntity;
import com.tma.recruit.model.entity.UserEntity;
import com.tma.recruit.model.enums.TokenType;
import com.tma.recruit.model.mapper.UserMapper;
import com.tma.recruit.model.request.ChangePasswordRequest;
import com.tma.recruit.model.request.LoginRequest;
import com.tma.recruit.model.request.ResetPasswordRequest;
import com.tma.recruit.model.request.UserRequest;
import com.tma.recruit.model.response.LoginResponse;
import com.tma.recruit.repository.TokenRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public ResponseEntity<?> create(String token, UserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ACCOUNT ALREADY EXISTS");
        }
        UserEntity creator = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        UserEntity userEntity = userMapper.toEntity(request);
        userEntity.setEmail(userEntity.getEmail().toLowerCase());
        userEntity.setPassword(encoder.encode(request.getPassword()));
        userEntity.setCreatedBy(creator);
        userEntity.setUpdatedBy(creator);
        userEntity = userRepository.save(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(userEntity));
    }

    @Override
    public ResponseEntity<?> update(String token, Long id, UserRequest request) {
        UserEntity userEntity = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        UserEntity updater = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        userMapper.partialUpdate(userEntity, request);
        userEntity.setUpdatedBy(updater);
        userEntity.setUpdatedDate(new Date());
        userEntity = userRepository.save(userEntity);
        return ResponseEntity.ok(userMapper.toResponse(userEntity));
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        try {
            UserEntity updater = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            UserEntity userEntity = userRepository.findByIdAndActiveTrue(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            userEntity.setActive(false);
            userEntity.setUpdatedDate(new Date());
            userEntity.setUpdatedBy(updater);
            userRepository.save(userEntity);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getAll() {
        List<UserEntity> userEntityList = userRepository.findByActiveTrue();
        return ResponseEntity.ok(userMapper.toResponse(userEntityList));
    }

    @Override
    public ResponseEntity<?> getById(String token, Long id) {
        Optional<UserEntity> entityOptional = userRepository.findByIdAndActiveTrue(id);
        if (!entityOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userMapper.toResponse(entityOptional.get()));
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCaseAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            TokenEntity tokenEntity = new TokenEntity(jwt, TokenType.ACCESS_TOKEN, userEntity);
            tokenRepository.save(tokenEntity);
            return ResponseEntity.ok(new LoginResponse(jwt));
        } catch (BadCredentialsException badCredentialsException) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "WRONG_PASSWORD");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> forgotPassword(String email) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCaseAndActiveTrue(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String token = UUID.randomUUID().toString();
        TokenEntity tokenEntity = new TokenEntity(token, TokenType.PASSWORD_RESET_TOKEN, userEntity);
        tokenRepository.save(tokenEntity);
//        mailService.sendForgotPasswordByMail( email, token);
        return ResponseEntity.ok(token);
    }

    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCaseAndActiveTrue(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TokenEntity tokenEntity = tokenRepository.findByToken(resetPasswordRequest.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (userEntity.getId().equals(tokenEntity.getUserEntity().getId())) {
            userEntity.setUpdatedDate(new Date());
            userEntity.setPassword(encoder.encode(resetPasswordRequest.getPassword()));
            userRepository.save(userEntity);
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<?> logout(String token) {
        TokenEntity tokenEntity = tokenRepository.findByToken(jwtUtils.parseJwtString(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        tokenRepository.delete(tokenEntity);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getProfile(String token) {
        String email = jwtUtils.getEmailFromJwtToken(token);
        UserEntity userEntity = userRepository.findByEmailIgnoreCaseAndActiveTrue(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(userMapper.toResponse(userEntity));
    }

    @Override
    public ResponseEntity<?> changePassword(String token, ChangePasswordRequest changePasswordRequest) {
        UserEntity userEntity = userRepository.findByIdAndActiveTrue(jwtUtils.getUserIdFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (encoder.matches(changePasswordRequest.getOldPassword(), userEntity.getPassword())) {
            userEntity.setUpdatedDate(new Date());
            userEntity.setUpdatedBy(userEntity);
            userEntity.setPassword(encoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(userEntity);
            List<TokenEntity> tokenEntities = tokenRepository.findByUserEntityId(userEntity.getId());
            tokenRepository.deleteAll(tokenEntities);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}