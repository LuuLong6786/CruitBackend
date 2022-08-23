package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.enums.QuestionTemplateStatus;
import com.tma.recruit.model.enums.QuestionTemplateType;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.request.QuestionTemplateRequest;
import org.springframework.http.ResponseEntity;

public interface IQuestionTemplateService {

    ResponseEntity<?> createPersonalTemplate(String token, QuestionTemplateRequest request);

    ResponseEntity<?> createSharingTemplate(String token, QuestionTemplateRequest request);

    ResponseEntity<?> update(String token, Long id, QuestionTemplateRequest request);

    ResponseEntity<?> delete(String token, Long id);

    ResponseEntity<?> filterByAdmin(String keyword, QuestionTemplateStatus status, Long categoryId,
                                    SortType sortType, String sortBy, Integer page,
                                    Integer pageSize);

    ResponseEntity<?> filterByUser(String token, String keyword, Boolean isPublic, Long categoryId,
                                   QuestionTemplateType templateType, SortType sortType, String sortBy,
                                   Integer page, Integer pageSize);

    ResponseEntity<?> getById(String token, Long id);

    ResponseEntity<?> approve(String token, Long id);

    ResponseEntity<?> reject(String token, Long id);

    ResponseEntity<?> submitToQueue(String token, Long id);

    ResponseEntity<?> explore(String token, Long categoryId, String keyword, SortType sortType, String sortBy,
                              Integer page, Integer pageSize);

    ResponseEntity<?> cloneTemplate(String token, Long id);

    ResponseEntity<?> updateStatus(String token, Long id, QuestionTemplateRequest request);
}