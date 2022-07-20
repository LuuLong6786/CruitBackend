package com.tma.recruit.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {

    private Long id;

    private String name;

    private String badgeId;

    private String email;

    private String username;

    private List<RoleResponse> roles;

    private Date createdDate;

    private Date updatedDate;

    private UserResponse author;

    private UserResponse updatedUser;

    private Boolean enable;
}