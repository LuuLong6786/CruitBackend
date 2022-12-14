package com.tma.recruit.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private String name;

    private Long id;

    private String badgeId;

    @Email
    private String email;

    private String username;

    @Size(min = 8, max = 50, message = "Password length must be at least 8 characters")
    private String password;

    private List<RoleRequest> roles;
}