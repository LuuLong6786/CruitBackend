package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.QuestionTemplate;
import com.tma.recruit.model.request.QuestionTemplateRequest;
import com.tma.recruit.model.response.QuestionTemplateResponse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {})
public interface QuestionTemplateMapper extends EntityMapper<QuestionTemplate, QuestionTemplateResponse,
        QuestionTemplateRequest> {

    @Named("partialUpdate")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "category")
    @Mapping(ignore = true, target = "questionBankTemplates")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget QuestionTemplate entity, QuestionTemplateRequest model);
//    QuestionTemplate toEntity(QuestionTemplateRequest questionTemplateRequest);
}