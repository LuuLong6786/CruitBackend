package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.request.RoleRequest;
import org.springframework.http.ResponseEntity;

public interface IRoleService {

    ResponseEntity<?> create(String token, RoleRequest request);

    ResponseEntity<?> update(String token, RoleRequest request, Long id);

    ResponseEntity<?> inactive(String token, Long id);

    ResponseEntity<?> getAll();

    ResponseEntity<?> getById(Long id);
}