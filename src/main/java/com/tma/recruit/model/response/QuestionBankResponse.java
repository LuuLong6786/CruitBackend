package com.tma.recruit.model.response;

import com.tma.recruit.model.enums.QuestionLevelEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankResponse {

    private Long id;

    private QuestionLevelEnum level;

    private Date createdDate;

    private Date updatedDate;

    private UserResponse author;

    private UserResponse updatedUser;

    private QuestionCategoryResponse category;

    private String content;

    private String answer;

    private Date approvedDate;

    private UserResponse approver;

    private boolean approved;

    private List<QuestionCriterionResponse> criteria;
}
