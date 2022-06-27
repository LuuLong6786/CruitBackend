package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.QuestionCategory;
import com.tma.recruit.model.request.QuestionCategoryRequest;
import com.tma.recruit.model.response.QuestionCategoryResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {})
public interface QuestionCategoryMapper
        extends EntityMapper<QuestionCategory, QuestionCategoryResponse, QuestionCategoryRequest> {
}