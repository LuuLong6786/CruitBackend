package com.tma.recruit.repository;

import com.tma.recruit.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailIgnoreCaseAndActiveTrue(String email);

    List<UserEntity> findByActiveTrue();

    Optional<UserEntity> findByIdAndActiveTrue(Long id);

    boolean existsByEmailIgnoreCaseAndActiveTrue(String email);

    boolean existsByEmailIgnoreCase(String email);
}
