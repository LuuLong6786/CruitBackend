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

    @Query(value = "SELECT users.* " +
            "FROM users, user_role, role " +
            "WHERE users.id = user_role.user_id " +
            "AND role.id = user_role.role_id " +
            "AND users.active = true " +
            "AND (users.name  LIKE CONCAT('%',:name,'%') or :name is null) " +
            "AND (users.username  LIKE CONCAT('%',:username,'%') or :username is null) " +
            "AND (users.email  LIKE CONCAT('%',:email,'%') or :email is null) " +
            "AND (role.id=:id or :id is null) " +
            "AND users.active = :active " +
            "GROUP BY users.id",
            nativeQuery = true)
    Page<User> filter(Boolean active, String name, String username, String email, Long id, Pageable paging);

    List<User> findByRolesNameContainingIgnoreCaseAndActiveTrue(String name);

    @Query(value = "SELECT IF(role.name = :role, 'true', 'false') as status " +
            "FROM users , role, user_role " +
            "where " +
            "users.id = user_role.user_id " +
            "AND role.id = user_role.role_id " +
            "AND users.id = :userId " +
            "GROUP BY users.id",
            nativeQuery = true)
    boolean checkRole(String role, Long userId);
}