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

    Optional<User> findByUsernameIgnoreCaseAndActiveTrue(String username);

    List<User> findByActiveTrue();

    Optional<User> findByIdAndActiveTrue(Long id);

    boolean existsByEmailIgnoreCaseAndActiveTrue(String email);

    boolean existsByUsernameIgnoreCaseAndActiveTrue(String username);

    boolean existsByBadgeIdIgnoreCaseAndActiveTrue(String badgeId);

    Page<User> findByNameContainingIgnoreCaseAndActiveTrue(String keyword, Pageable paging);

    Page<User> findByActiveTrue(Pageable paging);

    @Query(value = "SELECT u " +
            "FROM User u " +
            "JOIN u.roles r " +
            "WHERE " +
            "(u.name LIKE CONCAT('%',:name,'%') OR :name is null) " +
            "AND (u.username  LIKE CONCAT('%',:username,'%') OR :username is null) " +
            "AND (u.email  LIKE CONCAT('%',:email,'%') OR :email is null) " +
            "AND (r.id=:id OR :id is null) " +
            "AND u.active = :active " +
            "GROUP BY u.id")
    Page<User> filter(Boolean active, String name, String username, String email, Long id, Pageable paging);

    List<User> findByRolesNameContainingIgnoreCaseAndActiveTrue(String name);

    @Query(value = "SELECT CASE WHEN (r.name = :role) THEN 'true' ELSE 'false' END AS status " +
            "FROM User u " +
            "JOIN u.roles r " +
            "WHERE " +
            "u.id = :userId " +
            "GROUP BY u.id")
    boolean checkRole(String role, Long userId);
}