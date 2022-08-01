package com.tma.recruit.repository;

import com.tma.recruit.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    boolean existsByPermissionKeyAndActiveTrue(String permissionKey);

    Optional<Permission> findByIdAndActiveTrue(Long id);

    List<Permission> findByActiveTrue();
}
