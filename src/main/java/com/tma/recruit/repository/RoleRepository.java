package com.tma.recruit.repository;

import com.tma.recruit.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByNameAndEnableTrue(String name);

    Optional<Role> findByIdAndEnableTrue(Long id);

    List<Role> findByEnableTrue();

    Optional<Role> findByName(String name);

    Optional<Role> findByNameIgnoreCase(String guest);
}
