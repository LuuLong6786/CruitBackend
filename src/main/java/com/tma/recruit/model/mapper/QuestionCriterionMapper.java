package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.QuestionCriterion;
import com.tma.recruit.model.request.QuestionCriterionRequest;
import com.tma.recruit.model.response.QuestionCriterionResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {})
public interface QuestionCriterionMapper
        extends EntityMapper<QuestionCriterion, QuestionCriterionResponse, QuestionCriterionRequest> {
}