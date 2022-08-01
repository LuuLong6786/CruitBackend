package com.tma.recruit.controller;

import com.tma.recruit.anotation.OnlyAdmin;
import com.tma.recruit.model.request.ChangePasswordRequest;
import com.tma.recruit.model.request.LoginRequest;
import com.tma.recruit.model.request.ResetPasswordRequest;
import com.tma.recruit.model.request.UserRequest;
import com.tma.recruit.service.interfaces.IUserService;
import com.tma.recruit.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @OnlyAdmin
    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return userService.getAll();
    }

    @OnlyAdmin
    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestParam(required = false, defaultValue = "true") Boolean active,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String username,
                                    @RequestParam(required = false) String email,
                                    @RequestParam(required = false) Long roleId,
                                    @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                                    @RequestParam(required = false, defaultValue = "1") Integer page) {
        return userService.filter(active, name, username, email, roleId, pageSize, page);
    }

    @OnlyAdmin
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return userService.getById(token, id);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserRequest request) {
        return userService.create(null, request);
    }

    @OnlyAdmin
    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @Valid @RequestBody UserRequest request) {
        return userService.create(token, request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @Valid @RequestBody UserRequest request,
                                    @PathVariable Long id) {
        return userService.update(token, id, request);
    }

    @OnlyAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return userService.delete( id);
    }

    @OnlyAdmin
    @DeleteMapping("/inactive/{id}")
    public ResponseEntity<?> inactive(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return userService.inactive(token, id);
    }

    @OnlyAdmin
    @PutMapping("/active/{id}")
    public ResponseEntity<?> activeUser(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                        @PathVariable Long id) {
        return userService.active(token, id);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        return userService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        return userService.resetPassword(resetPasswordRequest);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                            @RequestBody ChangePasswordRequest changePasswordRequest) {
        return userService.changePassword(token, changePasswordRequest);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return userService.logout(token);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return userService.getProfile(token);
    }
}