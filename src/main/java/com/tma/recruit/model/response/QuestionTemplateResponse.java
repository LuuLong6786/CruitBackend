package com.tma.recruit.model.response;

import com.tma.recruit.model.entity.QuestionBankTemplate;
import com.tma.recruit.model.entity.QuestionCategory;
import com.tma.recruit.model.enums.QuestionTemplateStatus;
import com.tma.recruit.model.enums.QuestionTemplateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionTemplateResponse {

    private Long id;

    private String name;

    private String description;

    private boolean isPublic;

    private QuestionCategoryResponse category;

    private QuestionTemplateType questionTemplateType;

    private QuestionTemplateStatus status;

    private List<QuestionBankTemplateResponse> questionBankTemplates;

    private Date createdDate;

    private Date updatedDate;

    private UserResponse author;

    private UserResponse updatedUser;
}