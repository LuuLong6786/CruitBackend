package com.tma.recruit.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCategoryResponse {

    private Long id;

    private Date createdDate;

    private Date updatedDate;

    private UserResponse author;

    private UserResponse updatedUser;

    private String name;
}
