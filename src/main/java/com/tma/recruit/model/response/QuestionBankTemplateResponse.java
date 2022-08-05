package com.tma.recruit.model.response;

import com.tma.recruit.model.entity.QuestionBank;
import com.tma.recruit.model.entity.QuestionTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBankTemplateResponse {

    private Integer questionNo;

    private QuestionBankResponse question;
}