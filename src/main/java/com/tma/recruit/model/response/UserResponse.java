package com.tma.recruit.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String name;

    private String badgeId;

    private String username;

    private String email;

    private Date createdDate;

    private Date updatedDate;
}