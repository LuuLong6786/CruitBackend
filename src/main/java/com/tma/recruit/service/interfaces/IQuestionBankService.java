package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.enums.QuestionLevelEnum;
import com.tma.recruit.model.request.QuestionBankRequest;
import org.springframework.http.ResponseEntity;

public interface IQuestionBankService {

    ResponseEntity<?> create(String token, QuestionBankRequest request);

    ResponseEntity<?> update(String token, QuestionBankRequest request, Long id);

    ResponseEntity<?> delete(String token, Long id);

    ResponseEntity<?> getAll();

    ResponseEntity<?> getApprovedQuestion();

    ResponseEntity<?> getById(Long id);

    ResponseEntity<?> approve(String token, Long id);

    ResponseEntity<?> reject(String token, Long id);

    ResponseEntity<?> filter(QuestionLevelEnum level, Long categoryId, Long criterionId, Integer pageSize, Integer page, String keyword);
}
