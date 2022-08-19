package com.tma.recruit.model.request;

import com.tma.recruit.model.enums.QuestionTemplateStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionTemplateRequest {

    private Long id;

    private String name;

    private String description;

    private boolean isPublic = false;

    private List<QuestionBankTemplateRequest> questionBankTemplates;

    private QuestionCategoryRequest category;

    private QuestionTemplateStatus status;
}