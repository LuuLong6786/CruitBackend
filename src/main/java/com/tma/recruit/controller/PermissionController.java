package com.tma.recruit.controller;

import com.tma.recruit.anotation.OnlyAdmin;
import com.tma.recruit.model.request.PermissionRequest;
import com.tma.recruit.service.interfaces.IPermissionService;
import com.tma.recruit.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Autowired
    private IPermissionService permissionService;

    @OnlyAdmin
    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody PermissionRequest request) {
        return permissionService.create(token, request);
    }

    @OnlyAdmin
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody PermissionRequest request,
                                    @PathVariable Long id) {
        return permissionService.update(token, request, id);
    }

    @OnlyAdmin
    @DeleteMapping("/inactive/{id}")
    public ResponseEntity<?> inactive(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                      @PathVariable Long id) {
        return permissionService.inactive(token, id);
    }

    @OnlyAdmin
    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return permissionService.getAll();
    }

    @OnlyAdmin
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return permissionService.getById(id);
    }
}