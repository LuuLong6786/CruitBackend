package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.request.QuestionCategoryRequest;
import org.springframework.http.ResponseEntity;

public interface IQuestionCategoryService {

    ResponseEntity<?> create(String token, QuestionCategoryRequest request);

    ResponseEntity<?> update(String token, QuestionCategoryRequest request, Long id);

    ResponseEntity<?> delete(String token, Long id);

    ResponseEntity<?> getAll();

    ResponseEntity<?> getById(Long id);
}
