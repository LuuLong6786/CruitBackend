package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.enums.QuestionLevel;
import com.tma.recruit.model.enums.QuestionStatus;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.request.QuestionBankRequest;
import org.springframework.http.ResponseEntity;

public interface IQuestionBankService {

    ResponseEntity<?> create(String token, QuestionBankRequest request);

    ResponseEntity<?> update(String token, QuestionBankRequest request, Long id);

    ResponseEntity<?> inactive(String token, Long id);

    ResponseEntity<?> getAll();

    ResponseEntity<?> getApprovedQuestion();

    ResponseEntity<?> getById(String token, Long id);

    ResponseEntity<?> approve(String token, Long id);

    ResponseEntity<?> reject(String token, Long id);

    ResponseEntity<?> filter(String token, QuestionStatus status, QuestionLevel level, Long categoryId,
                             Long criterionId, Integer pageSize, Integer page, String keyword, SortType sortType,
                             String sortBy);

    ResponseEntity<?> delete(Long id);
}
