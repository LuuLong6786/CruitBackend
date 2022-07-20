package com.tma.recruit.model.response;

import com.tma.recruit.model.enums.QuestionLevel;
import com.tma.recruit.model.enums.QuestionStatus;
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

    private QuestionLevel level;

    private Date createdDate;

    private Date updatedDate;

    private UserResponse author;

    private UserResponse updatedUser;

    private QuestionCategoryResponse category;

    private String content;

    private String answer;

    private Date approvedDate;

    private UserResponse approver;

    private QuestionStatus status;

    private List<QuestionCriterionResponse> criteria;

    private Boolean enable = true;
}
