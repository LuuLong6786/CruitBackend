package com.tma.recruit.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCriterionRequest {

    private Long id;

    private String name;

    private Boolean enable;
}