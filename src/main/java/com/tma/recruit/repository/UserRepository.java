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

    @Query(value = "SELECT users.* \n" +
            "FROM users, user_role, role\n" +
            "WHERE users.id = user_role.user_id\n" +
            "AND role.id = user_role.role_id\n" +
            "AND users.active = true\n" +
            "AND (users.name like CONCAT('%',:name,'%') or :name is null)\n" +
            "AND (users.username like CONCAT('%',:username,'%') or :username is null)\n" +
            "AND (users.email like CONCAT('%',:email,'%') or :email is null)\n" +
            "AND (role.id=:id or :id is null)\n" +
            "AND users.active = :active\n" +
            "GROUP BY users.id",
            nativeQuery = true)
    Page<User> filter(Boolean active, String name, String username, String email, Long id, Pageable paging);

    List<User> findByRolesNameContainingIgnoreCaseAndActiveTrue(String name);

    @Query(value = "SELECT IF(role.name = :role, 'true', 'false') as status\n" +
            "FROM users , role, user_role\n" +
            "where\n" +
            "users.id = user_role.user_id\n" +
            "AND role.id = user_role.role_id\n" +
            "AND users.id = :userId\n" +
            "GROUP BY users.id",
            nativeQuery = true)
    boolean checkRole(String role, Long userId);
}