package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.enums.QuestionTemplateStatus;
import com.tma.recruit.model.enums.QuestionTemplateType;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.request.QuestionTemplateRequest;
import org.springframework.http.ResponseEntity;

public interface IQuestionTemplateService {

    ResponseEntity<?> createPersonalTemplate(String token, QuestionTemplateRequest request);  // ok

    ResponseEntity<?> createSharingTemplate(String token, QuestionTemplateRequest request);

    ResponseEntity<?> update(String token, Long id, QuestionTemplateRequest request); // ok

    ResponseEntity<?> delete(String token, Long id);    // ok

    ResponseEntity<?> getAll();      // X

    ResponseEntity<?> filter(String token, String keyword, Boolean isPublic, QuestionTemplateStatus status,
                             Long categoryId, QuestionTemplateType templateType, SortType sortType,
                             String sortBy, Integer page, Integer pageSize); // ok

    ResponseEntity<?> filterByAdmin(String keyword, QuestionTemplateStatus status, Long categoryId,
                                    QuestionTemplateType templateType, SortType sortType, String sortBy, Integer page,
                                    Integer pageSize); // ok

    ResponseEntity<?> filterByUser(String token, String keyword, Boolean isPublic, Long categoryId,
                                   QuestionTemplateType templateType, SortType sortType, String sortBy,
                                   Integer page, Integer pageSize); // ok

    ResponseEntity<?> getById(String token, Long id);   // ok

    ResponseEntity<?> approve(String token, Long id);   // ok

    ResponseEntity<?> reject(String token, Long id);    // ok

    ResponseEntity<?> share(String token, Long id);     // ok

    ResponseEntity<?> explore(String token, String keyword, SortType sortType, String sortBy, Integer page,     // yet
                              Integer pageSize);

    ResponseEntity<?> pullTemplate(String token, Long id);      // ok

    ResponseEntity<?> updateStatus(String token, Long id, QuestionTemplateRequest request);
}