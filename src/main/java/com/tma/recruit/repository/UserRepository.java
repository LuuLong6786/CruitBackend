package com.tma.recruit.repository;

import com.tma.recruit.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCaseAndActiveTrue(String email);

    List<User> findByActiveTrue();

    Optional<User> findByIdAndActiveTrue(Long id);

    boolean existsByEmailIgnoreCaseAndActiveTrue(String email);

    boolean existsByEmailIgnoreCase(String email);

    Page<User> findByNameContainingIgnoreCaseAndActiveTrue(String keyword, Pageable paging);

    Page<User> findByActiveTrue(Pageable paging);

    @Query(value = "select u.* from users u , user_role ur, role r " +
            "where u.id = ur.user_id and r.id= ur.role_id and u.active=true " +
            "and (u.name like CONCAT('%',:keyword,'%') or :keyword is null) " +
            "and (r.id=:id or :id is null)  " +
            "group by u.id",
            nativeQuery = true)
    Page<User> filter(String keyword, Long id, Pageable paging);
}