package com.tma.recruit.model.entity;

import com.tma.recruit.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Email
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @OneToMany(mappedBy = "userEntity")
    private List<TokenEntity> tokenEntities;

    public UserEntity(String email, String password, UserRole role) {
        this.email=email;
        this.password=password;
        this.role=role;
    }
}