package com.tma.recruit.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    private Long id;

    private String name;

    private List<PermissionResponse> permissions;

    private Date createdDate;

    private Date updatedDate;

    private UserResponse author;

    private UserResponse updatedUser;
}