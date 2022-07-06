package com.tma.recruit.controller;

import com.tma.recruit.model.request.ChangePasswordRequest;
import com.tma.recruit.model.request.LoginRequest;
import com.tma.recruit.model.request.ResetPasswordRequest;
import com.tma.recruit.model.request.UserRequest;
import com.tma.recruit.service.interfaces.IUserService;
import com.tma.recruit.util.Constant;
import com.tma.recruit.util.PreAuthorizerConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return userService.getAll();
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) Long roleId,
                                    @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                                    @RequestParam(required = false, defaultValue = "1") Integer page) {
        return userService.filter(keyword, roleId, pageSize, page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserRequest request) {
        return userService.create(null, request);
    }

    @PreAuthorize(PreAuthorizerConstant.ADMIN_ROLE)
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

    @PreAuthorize(PreAuthorizerConstant.ADMIN_ROLE)
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return userService.delete(token, id);
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