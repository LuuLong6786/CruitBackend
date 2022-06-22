package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.Role;
import com.tma.recruit.model.entity.Token;
import com.tma.recruit.model.entity.User;
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
import java.util.stream.Collectors;

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
        Optional<User> author = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token));
        User user = userMapper.toEntity(request);
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(encoder.encode(request.getPassword()));
        author.ifPresent(user::setAuthor);
        author.ifPresent(user::setUpdatedUser);
        user = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(user));
    }

    @Override
    public ResponseEntity<?> update(String token, Long id, UserRequest request) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User updater = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        userMapper.partialUpdate(user, request);
        user.setUpdatedUser(updater);
        user.setUpdatedDate(new Date());
        user = userRepository.save(user);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        try {
            User updater = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            User user = userRepository.findByIdAndActiveTrue(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            user.setActive(false);
            user.setUpdatedDate(new Date());
            user.setUpdatedUser(updater);
            userRepository.save(user);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getAll() {
        List<User> users = userRepository.findByActiveTrue();
        return ResponseEntity.ok(userMapper.toResponse(users));
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        Optional<User> entityOptional = userRepository.findByIdAndActiveTrue(id);
        if (!entityOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userMapper.toResponse(entityOptional.get()));
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCaseAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            Token token = new Token(jwt, TokenType.ACCESS_TOKEN, user);
            tokenRepository.save(token);
            LoginResponse loginResponse = new LoginResponse(jwt, user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException badCredentialsException) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "WRONG_PASSWORD");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> forgotPassword(String email) {
        User user = userRepository.findByEmailIgnoreCaseAndActiveTrue(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String tokenString = UUID.randomUUID().toString();
        Token token = new Token(tokenString, TokenType.PASSWORD_RESET_TOKEN, user);
        tokenRepository.save(token);
//        mailService.sendForgotPasswordByMail( email, token);
        return ResponseEntity.ok(token);
    }

    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository.findByEmailIgnoreCaseAndActiveTrue(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Token token = tokenRepository.findByToken(resetPasswordRequest.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (user.getId().equals(token.getUser().getId())) {
            user.setUpdatedDate(new Date());
            user.setPassword(encoder.encode(resetPasswordRequest.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<?> logout(String token) {
        Token tokenEntity = tokenRepository.findByToken(jwtUtils.parseJwtString(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        tokenRepository.delete(tokenEntity);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getProfile(String token) {
        User user = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Override
    public ResponseEntity<?> changePassword(String token, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByIdAndActiveTrue(jwtUtils.getUserIdFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (encoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            user.setUpdatedDate(new Date());
            user.setUpdatedUser(user);
            user.setPassword(encoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(user);
            List<Token> tokens = tokenRepository.findByUserId(user.getId());
            tokenRepository.deleteAll(tokens);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}