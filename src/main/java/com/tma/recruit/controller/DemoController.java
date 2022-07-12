package com.tma.recruit.controller;

import com.tma.recruit.model.entity.Role;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.mapper.RoleMapper;
import com.tma.recruit.model.mapper.UserMapper;
import com.tma.recruit.model.request.RoleRequest;
import com.tma.recruit.model.request.UserRequest;
import com.tma.recruit.model.response.UserNotificationResponse;
import com.tma.recruit.repository.QuestionBankRepository;
import com.tma.recruit.repository.RoleRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.INotificationService;
import com.tma.recruit.util.Constant;
import com.tma.recruit.util.RoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @PostMapping("/user")
    public ResponseEntity<?> test(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token, @RequestBody UserRequest request) {
        User author;
        if (token != null) {
            author = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        } else {
            author = null;
        }

        List<Role> roles = new ArrayList<>();
        if (request.getRoles() != null && request.getRoles().size() > 0) {
            for (RoleRequest roleRequest : request.getRoles()) {
                if (roleRequest.getId() != null && roleRequest.getId() > 0) {
                    Role role = roleRepository.findByIdAndActiveTrue(roleRequest.getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                    if (!role.getName().equals(RoleConstant.ADMIN)
                            || Objects.requireNonNull(author).getRoles().stream()
                            .anyMatch(role1 -> role1.getName().equals(RoleConstant.ADMIN))){
                        roles.add(role);
                    }
                }
            }
        } else {
            Role role = roleRepository.findByNameIgnoreCase(RoleConstant.GUEST)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            roles.add(role);
        }



        return ResponseEntity.ok(roleMapper.toResponse(roles));
    }

    @GetMapping("/logout")
    public List<String> logout(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return jwtUtils.getRoleFromToken(token);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String testAdmin(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return "userService.getProfile(token)";
    }

    @PreAuthorize("hasAuthority('PM')")
    @GetMapping("/pm")
    public String testPM(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return "userService.getProfile(token)";
    }

    @PreAuthorize("hasAuthority('ENGINEER')")
    @GetMapping("/engineer")
    public String testENGINEER(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return "userService.getProfile(token)";
    }

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SimpMessagingTemplate template;

    @GetMapping("/test-notification")
    public ResponseEntity<?> testNotification(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,@RequestParam String message){
        List<User> admins = userRepository.findByRolesNameContainingIgnoreCaseAndActiveTrue(RoleConstant.ADMIN);

        User user = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

//        List<User> users=userRepository.findByActiveTrue();

//        notificationService.notifyForUserCreation(users.get(0));

        UserNotificationResponse response =new UserNotificationResponse();
        response.setContent(message);
//        response.setContent("User "+user.getUsername()+" has been created");
        response.setUser(userMapper.toResponse(user));
        response.setTime(new Date());

//        template.convertAndSendToUser(RoleConstant.ADMIN, "/queue/notification", response);

        template.convertAndSendToUser(user.getUsername(), "/queue/notification", response);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}