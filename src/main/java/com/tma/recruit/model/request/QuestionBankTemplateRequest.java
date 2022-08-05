package com.tma.recruit.model.request;

import com.tma.recruit.model.response.QuestionBankResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBankTemplateRequest {

    private Long questionId;
}
