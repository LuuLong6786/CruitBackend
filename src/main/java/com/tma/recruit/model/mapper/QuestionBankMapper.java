package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.QuestionBank;
import com.tma.recruit.model.request.QuestionBankRequest;
import com.tma.recruit.model.response.QuestionBankResponse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {})
public interface QuestionBankMapper
        extends EntityMapper<QuestionBank, QuestionBankResponse, QuestionBankRequest> {

    @Mapping(ignore = true, target = "category")
    @Mapping(ignore = true, target = "approver")
    @Mapping(ignore = true, target = "criteria")
    @Mapping(ignore = true, target = "id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget QuestionBank entity, QuestionBankRequest model);

    @Mapping(ignore = true, target = "approver")
    @Mapping(ignore = true, target = "category")
    @Mapping(ignore = true, target = "criteria")
    QuestionBank toEntity(QuestionBankRequest questionBankRequest);

    QuestionBankResponse toResponse(QuestionBank questionBank);
}