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

//    Optional<User> findByEmailIgnoreCaseAndActiveTrue(String email);

    Optional<User> findByUsernameIgnoreCaseAndActiveTrue(String username);

    List<User> findByActiveTrue();

    Optional<User> findByIdAndActiveTrue(Long id);

    boolean existsByEmailIgnoreCaseAndActiveTrue(String email);

    boolean existsByUsernameIgnoreCaseAndActiveTrue(String username);

    boolean existsByBadgeIdIgnoreCaseAndActiveTrue(String badgeId);

    Page<User> findByNameContainingIgnoreCaseAndActiveTrue(String keyword, Pageable paging);

    Page<User> findByActiveTrue(Pageable paging);

    @Query(value = "select users.* from users , user_role, role \n" +
            "where users.id = user_role.user_id and role.id= user_role.role_id and users.active=true \n" +
            "and (users.name like CONCAT('%',:keyword,'%') or :keyword is null) \n" +
            "and (role.id=:id or :id is null)  " +
            "group by users.id",
            nativeQuery = true)
    Page<User> filter(String keyword, Long id, Pageable paging);

    List<User> findByRolesNameContainingAndActiveTrue(String name);

}