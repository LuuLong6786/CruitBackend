package com.tma.recruit.repository;

import com.tma.recruit.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    boolean existsByPermissionKeyAndEnableTrue(String permissionKey);

    Optional<Permission> findByIdAndEnableTrue(Long id);

    List<Permission> findByEnableTrue();
}
