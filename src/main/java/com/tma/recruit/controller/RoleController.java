package com.tma.recruit.controller;

import com.tma.recruit.anotation.OnlyAdmin;
import com.tma.recruit.model.request.RoleRequest;
import com.tma.recruit.service.interfaces.IRoleService;
import com.tma.recruit.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @OnlyAdmin
    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody RoleRequest request) {
        return roleService.create(token, request);
    }

    @OnlyAdmin
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody RoleRequest request,
                                    @PathVariable Long id) {
        return roleService.update(token, request, id);
    }

    @OnlyAdmin
    @DeleteMapping("/inactive/{id}")
    public ResponseEntity<?> inactive(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                      @PathVariable Long id) {
        return roleService.inactive(token, id);
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return roleService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return roleService.getById(id);
    }
}