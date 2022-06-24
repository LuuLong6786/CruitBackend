package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.QuestionBank;
import com.tma.recruit.model.request.QuestionBankRequest;
import com.tma.recruit.model.response.QuestionBankResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {})
public abstract class QuestionBankMapper
        implements EntityMapper<QuestionBank, QuestionBankResponse, QuestionBankRequest> {

    @Mapping(ignore = true, target = "category")
    @Mapping(ignore = true, target = "approver")
    @Mapping(ignore = true, target = "criteria")
    @Mapping(ignore = true, target = "id")
    public abstract void partialUpdate(@MappingTarget QuestionBank entity, QuestionBankRequest model);

    @Mapping(ignore = true,target = "approver")
    @Mapping(ignore = true,target = "category")
    @Mapping(ignore = true,target = "criteria")
    public abstract QuestionBank toEntity(QuestionBankRequest questionBankRequest);
}