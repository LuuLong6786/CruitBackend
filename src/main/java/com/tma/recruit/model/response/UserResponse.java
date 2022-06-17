package com.tma.recruit.model.response;

import com.tma.recruit.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String email;

    private UserRole role;

    private Date createdDate;

    private Date updatedDate;

    private UserResponse createdBy;

    private UserResponse updatedBy;
}