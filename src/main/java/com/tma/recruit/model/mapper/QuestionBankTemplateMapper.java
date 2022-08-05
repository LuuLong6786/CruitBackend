package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.QuestionBankTemplate;
import com.tma.recruit.model.response.QuestionBankTemplateResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {})
public interface QuestionBankTemplateMapper
        extends EntityMapper<QuestionBankTemplate, QuestionBankTemplateResponse, QuestionBankTemplateResponse> {

    QuestionBankTemplate toEntity(QuestionBankTemplateResponse model);
}