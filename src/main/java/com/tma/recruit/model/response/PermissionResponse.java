package com.tma.recruit.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponse {

    private Long id;

    private String name;

    private String permissionKey;

    private Date createdDate;

    private Date updatedDate;

    private UserResponse author;

    private UserResponse updatedUser;
}