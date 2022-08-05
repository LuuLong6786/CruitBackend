package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.request.ChangePasswordRequest;
import com.tma.recruit.model.request.LoginRequest;
import com.tma.recruit.model.request.ResetPasswordRequest;
import com.tma.recruit.model.request.UserRequest;
import org.springframework.http.ResponseEntity;

public interface IUserService {

    ResponseEntity<?> create(String token, UserRequest request);

    ResponseEntity<?> update(String token, Long id, UserRequest request);

    ResponseEntity<?> inactive(String token, Long id);

    ResponseEntity<?> getAll();

    ResponseEntity<?> getById(String token, Long id);

    ResponseEntity<?> login(LoginRequest request);

    ResponseEntity<?> forgotPassword(String email);

    ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordRequest);

    ResponseEntity<?> logout(String token);

    ResponseEntity<?> getProfile(String token);

    ResponseEntity<?> changePassword(String token, ChangePasswordRequest changePasswordRequest);

    ResponseEntity<?> filter(Boolean active, String name, String username, String email, Long roleId, Integer pageSize,
                             Integer page, SortType sortType, String sortBy);

    ResponseEntity<?> active(String token, Long id);

    ResponseEntity<?> delete(Long id);

    Boolean isAdmin(User user);
}
