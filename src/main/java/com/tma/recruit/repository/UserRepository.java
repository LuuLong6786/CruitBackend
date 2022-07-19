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

    Optional<User> findByUsernameIgnoreCaseAndEnableTrue(String username);

    List<User> findByEnableTrue();

    Optional<User> findByIdAndEnableTrue(Long id);

    boolean existsByEmailIgnoreCaseAndEnableTrue(String email);

    boolean existsByUsernameIgnoreCaseAndEnableTrue(String username);

    boolean existsByBadgeIdIgnoreCaseAndEnableTrue(String badgeId);

    Page<User> findByNameContainingIgnoreCaseAndEnableTrue(String keyword, Pageable paging);

    Page<User> findByEnableTrue(Pageable paging);

    @Query(value = "select users.* from users , user_role, role \n" +
            "where users.id = user_role.user_id and role.id= user_role.role_id and users.active=true \n" +
            "and (users.name like CONCAT('%',:name,'%') or :name is null) \n" +
            "and (users.username like CONCAT('%',:username,'%') or :username is null) \n" +
            "and (users.email like CONCAT('%',:email,'%') or :email is null) \n" +
            "and (role.id=:id or :id is null)  " +
            "group by users.id",
            nativeQuery = true)
    Page<User> filter(String name, String username, String email, Long id, Pageable paging);

    List<User> findByRolesNameContainingIgnoreCaseAndEnableTrue(String name);

}