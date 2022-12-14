package com.tma.recruit.model.request;

import com.tma.recruit.model.enums.QuestionLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankRequest {

    private Long id;

    private QuestionLevel level;

    private QuestionCategoryRequest category;

    private String content;

    private String answer;

    private UserRequest approver;

    private List<QuestionCriterionRequest> criteria;
}
