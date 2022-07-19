package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.request.QuestionCategoryRequest;
import org.springframework.http.ResponseEntity;

public interface IQuestionCategoryService {

    ResponseEntity<?> create(String token, QuestionCategoryRequest request);

    ResponseEntity<?> update(String token, QuestionCategoryRequest request, Long id);

    ResponseEntity<?> delete(String token, Long id);

    ResponseEntity<?> getAll(Boolean showDisabled);

    ResponseEntity<?> getById(Long id);

    ResponseEntity<?> filter(String keyword, Boolean enable, Integer pageSize, Integer page, SortType sortType, String sortBy);

    ResponseEntity<?> enable(String token, Long id);
}
