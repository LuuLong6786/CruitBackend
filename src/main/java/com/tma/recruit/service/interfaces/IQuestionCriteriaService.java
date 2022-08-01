package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.request.QuestionCriterionRequest;
import com.tma.recruit.model.request.UpdateActiveRequest;
import org.springframework.http.ResponseEntity;

public interface IQuestionCriteriaService {

    ResponseEntity<?> create(String token, QuestionCriterionRequest request);

    ResponseEntity<?> update(String token, QuestionCriterionRequest request, Long id);

    ResponseEntity<?> inactive(String token, Long id);

    ResponseEntity<?> getAll(Boolean showDisabled);

    ResponseEntity<?> getById(String token, Long id);

    ResponseEntity<?> filter(String keyword, Boolean active, Integer pageSize, Integer page, SortType sortType,
                             String sortBy);

    ResponseEntity<?> active(String token, Long id);

    ResponseEntity<?> updateActive(String token, Long id, UpdateActiveRequest active);

    ResponseEntity<?> delete(Long id);
}
