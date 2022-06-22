package com.tma.recruit.repository;

import com.tma.recruit.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByName(String name);

    Optional<Role> findByIdAndActiveTrue(Long id);

    List<Role> findByActiveTrue();
}
