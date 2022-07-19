package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.request.QuestionCriterionRequest;
import org.springframework.http.ResponseEntity;

public interface IQuestionCriteriaService {

    ResponseEntity<?> create(String token, QuestionCriterionRequest request);

    ResponseEntity<?> update(String token, QuestionCriterionRequest request, Long id);

    ResponseEntity<?> delete(String token, Long id);

    ResponseEntity<?> getAll();

    ResponseEntity<?> getById(Long id);

    ResponseEntity<?> filter(String keyword, Boolean enable, Integer pageSize, Integer page, SortType sortType, String sortBy);

    ResponseEntity<?> enable(String token, Long id);
}
