package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.request.PermissionRequest;
import org.springframework.http.ResponseEntity;

public interface IPermissionService {

    ResponseEntity<?> create(String token, PermissionRequest request);

    ResponseEntity<?> update(String token, PermissionRequest request, Long id);

    ResponseEntity<?> disable(String token, Long id);

    ResponseEntity<?> getAll();

    ResponseEntity<?> getById(Long id);
}
