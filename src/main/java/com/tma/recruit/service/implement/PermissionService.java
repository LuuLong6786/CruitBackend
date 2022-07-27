package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.Permission;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.mapper.PermissionMapper;
import com.tma.recruit.model.request.PermissionRequest;
import com.tma.recruit.repository.PermissionRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class PermissionService implements IPermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> create(String token, PermissionRequest request) {
        if (permissionRepository.existsByPermissionKeyAndEnableTrue(request.getPermissionKey())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        User author = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Permission permission = permissionMapper.toEntity(request);
        permission.setAuthor(author);
        permission.setUpdatedUser(author);
        permission = permissionRepository.save(permission);

        return ResponseEntity.status(HttpStatus.CREATED).body(permissionMapper.toResponse(permission));
    }

    @Override
    public ResponseEntity<?> update(String token, PermissionRequest request, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Permission permission = permissionRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getPermissionKey() != null && !permission.getPermissionKey().equals(request.getPermissionKey())) {
            if (permissionRepository.existsByPermissionKeyAndEnableTrue(request.getPermissionKey())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }

        permissionMapper.partialUpdate(permission, request);
        permission.setUpdatedUser(updater);
        permission.setUpdatedDate(new Date());
        permission = permissionRepository.save(permission);

        return ResponseEntity.ok(permissionMapper.toResponse(permission));
    }

    @Override
    public ResponseEntity<?> disable(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Permission permission = permissionRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        permission.setEnable(false);
        permission.setPermissionKey(null);
        permission.setUpdatedDate(new Date());
        permission.setUpdatedUser(updater);
        permissionRepository.save(permission);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(permissionMapper.toResponse(permissionRepository.findByEnableTrue()));
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        Permission permission = permissionRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(permissionMapper.toResponse(permission));
    }
}
