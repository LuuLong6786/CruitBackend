package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.Permission;
import com.tma.recruit.model.entity.Role;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.mapper.RoleMapper;
import com.tma.recruit.model.request.PermissionRequest;
import com.tma.recruit.model.request.RoleRequest;
import com.tma.recruit.repository.PermissionRepository;
import com.tma.recruit.repository.RoleRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class RoleService implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public ResponseEntity<?> create(String token, RoleRequest request) {
        if (roleRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        User author = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        List<Permission> permissions = new ArrayList<>();
        for (PermissionRequest permissionRequest : request.getPermissions()) {
            Permission permission = permissionRepository.findByIdAndActiveTrue(permissionRequest.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            permissions.add(permission);
        }

        Role role = roleMapper.toEntity(request);
        role.setAuthor(author);
        role.setUpdatedUser(author);
        role.setPermissions(permissions);
        role = roleRepository.save(role);

        return ResponseEntity.status(HttpStatus.CREATED).body(roleMapper.toResponse(role));
    }

    @Override
    public ResponseEntity<?> update(String token, RoleRequest request, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Role role = roleRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getName() != null && !role.getName().equals(request.getName())) {
            if (roleRepository.existsByNameAndActiveTrue(request.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }

        roleMapper.partialUpdate(role, request);
        if (request.getPermissions() != null && request.getPermissions().size() > 0) {
            List<Permission> permissions = new ArrayList<>();
            for (PermissionRequest permissionRequest : request.getPermissions()) {
                if (permissionRequest.getId() != null && permissionRequest.getId() > 0) {
                    Permission permission = permissionRepository.findByIdAndActiveTrue(permissionRequest.getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                    permissions.add(permission);
                }
            }
            role.setPermissions(permissions);
        }
        role.setUpdatedUser(updater);
        role.setUpdatedDate(new Date());

        role = roleRepository.save(role);

        return ResponseEntity.ok(roleMapper.toResponse(role));
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Role role = roleRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        role.setActive(false);
        role.setName(null);
        role.setUpdatedDate(new Date());
        role.setUpdatedUser(updater);
        roleRepository.save(role);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(roleMapper.toResponse(roleRepository.findByActiveTrue()));
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        Role role = roleRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(roleMapper.toResponse(role));
    }
}