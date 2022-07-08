package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.Role;
import com.tma.recruit.model.entity.Token;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.TokenType;
import com.tma.recruit.model.mapper.UserMapper;
import com.tma.recruit.model.request.*;
import com.tma.recruit.model.response.*;
import com.tma.recruit.repository.RoleRepository;
import com.tma.recruit.repository.TokenRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.INotificationService;
import com.tma.recruit.service.interfaces.IUserService;
import com.tma.recruit.util.RoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import java.util.*;
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

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private INotificationService notificationService;

    @Override
    public ResponseEntity<?> create(String token, UserRequest request) {
        validateUsername(request);

        if (request.getEmail() != null && userRepository.existsByEmailIgnoreCaseAndActiveTrue(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "EMAIL ALREADY EXISTS");
        }

        if (request.getBadgeId() != null && userRepository.existsByBadgeIdIgnoreCaseAndActiveTrue(request.getBadgeId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "BADGE ID ALREADY EXISTS");
        }

        if (userRepository.existsByUsernameIgnoreCaseAndActiveTrue(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ACCOUNT ALREADY EXISTS");
        }

        User author = null;
        if (token != null) {
            author = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        }

        List<Role> roles = new ArrayList<>();
        if (request.getRoles() != null && request.getRoles().size() > 0) {
            for (RoleRequest roleRequest : request.getRoles()) {
                if (roleRequest.getId() != null && roleRequest.getId() > 0) {
                    Role role = roleRepository.findByIdAndActiveTrue(roleRequest.getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

                    if (!role.getName().equals(RoleConstant.ADMIN)
                            || (author != null && author.getRoles().stream()
                            .anyMatch(role1 -> role1.getName().equals(RoleConstant.ADMIN)))) {
                        roles.add(role);
                    }
                }
            }
        } else {
            Role role = roleRepository.findByNameIgnoreCase(RoleConstant.GUEST)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            roles.add(role);
        }

        User user = userMapper.toEntity(request);
        user.setUsername(request.getUsername().toLowerCase());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setAuthor(author);
        user.setUpdatedUser(author);
        user.setRoles(roles);
        user = userRepository.save(user);
        notificationService.notifyCreationToAdmin(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDetailResponse(user));
    }

    @Override
    public ResponseEntity<?> update(String token, Long id, UserRequest request) {
        User user = userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Role> roles = new ArrayList<>();
        if (request.getRoles() != null && request.getRoles().size() > 0) {
            for (RoleRequest roleRequest : request.getRoles()) {
                if (roleRequest.getId() != null && roleRequest.getId() > 0) {
                    Role role = roleRepository.findByIdAndActiveTrue(roleRequest.getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                    roles.add(role);
                }
            }
        }

        userMapper.partialUpdate(user, request);
        user.setUpdatedUser(updater);
        user.setUpdatedDate(new Date());
        if (roles.size() > 0) {
            user.setRoles(roles);
        }
        user = userRepository.save(user);
        notificationService.notifyUpdateToAdmin(user);

        return ResponseEntity.ok(userMapper.toDetailResponse(user));
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        try {
            User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            User user = userRepository.findByIdAndActiveTrue(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            user.setActive(false);
            user.setUpdatedDate(new Date());
            user.setUpdatedUser(updater);
            user.setUsername(null);
            user.setEmail(null);
            userRepository.save(user);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getAll() {
        List<User> users = userRepository.findByActiveTrue();

        return ResponseEntity.ok(userMapper.toDetailResponse(users));
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        Optional<User> entityOptional = userRepository.findByIdAndActiveTrue(id);
        if (!entityOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(userMapper.toDetailResponse(entityOptional.get()));
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        User user = userRepository.findByUsernameIgnoreCaseAndActiveTrue(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ACCOUNT NOT EXISTS"));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            Token token = new Token(jwt, TokenType.ACCESS_TOKEN, user);
            tokenRepository.save(token);
            LoginResponse loginResponse = new LoginResponse(jwt,
                    user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toList()));

            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException badCredentialsException) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "WRONG_PASSWORD");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> forgotPassword(String email) {
        User user = userRepository.findByUsernameIgnoreCaseAndActiveTrue(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String tokenString = UUID.randomUUID().toString();
        Token token = new Token(tokenString, TokenType.PASSWORD_RESET_TOKEN, user);
        tokenRepository.save(token);
//        mailService.sendForgotPasswordByMail( email, token);

        return ResponseEntity.ok(token);
    }

    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        return null;
//        User user = userRepository.findByEmailIgnoreCaseAndActiveTrue(resetPasswordRequest.getEmail())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//
//        Token token = tokenRepository.findByToken(resetPasswordRequest.getToken())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//
//        if (user.getId().equals(token.getUser().getId())) {
//            user.setUpdatedDate(new Date());
//            user.setPassword(encoder.encode(resetPasswordRequest.getPassword()));
//            userRepository.save(user);
//
//            return ResponseEntity.ok(HttpStatus.OK);
//        } else {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
//        }
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
        User user = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(userMapper.toDetailResponse(user));
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

    @Override
    public ResponseEntity<?> filter(String name, String username, String email, Long roleId, Integer pageSize, Integer page) {
        Pageable paging = PageRequest.of(page - 1, pageSize);

        Page<User> users = userRepository.filter(name, username, email, roleId, paging);

        Pagination pagination = new Pagination(pageSize, page, users.getTotalPages(), users.getNumberOfElements());

        ModelPage<UserDetailResponse> modelPage = new ModelPage<>(userMapper.toDetailResponse(users.getContent()), pagination);

        return ResponseEntity.ok(modelPage);
    }

    public void validateUsername(UserRequest request){
        if (request.getUsername().replaceAll("\\s", "").length()<request.getUsername().length()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}