package com.tma.recruit.controller;

import com.tma.recruit.anotation.OnlyAdmin;
import com.tma.recruit.model.enums.QuestionTemplateStatus;
import com.tma.recruit.model.enums.QuestionTemplateType;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.request.QuestionTemplateRequest;
import com.tma.recruit.service.interfaces.IQuestionTemplateService;
import com.tma.recruit.util.Constant;
import com.tma.recruit.util.PaginationConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question-template")
public class QuestionTemplateController {

    @Autowired
    private IQuestionTemplateService questionTemplateService;

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionTemplateRequest request) {
        return questionTemplateService.createPersonalTemplate(token, request);
    }

    @PostMapping("/sharing")
    public ResponseEntity<?> createSharingTemplate(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                                   @RequestBody QuestionTemplateRequest request) {
        return questionTemplateService.createSharingTemplate(token, request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionTemplateRequest request,
                                    @PathVariable Long id) {
        return questionTemplateService.update(token, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return questionTemplateService.delete(token, id);
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return questionTemplateService.getAll();
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) Boolean isPublic,
                                    @RequestParam(required = false) QuestionTemplateStatus status,
                                    @RequestParam(required = false) Long categoryId,
                                    @RequestParam(required = false,
                                            defaultValue = "PERSONAL") QuestionTemplateType templateType,
                                    @RequestParam(required = false, defaultValue = "DESC") SortType sortType,
                                    @RequestParam(required = false, defaultValue = "id") String sortBy,
                                    @RequestParam(required = false,
                                            defaultValue = PaginationConstant.PAGE_SIZE_DEFAULT_VALUE) Integer pageSize,
                                    @RequestParam(required = false,
                                            defaultValue = PaginationConstant.PAGE_DEFAULT_VALUE) Integer page) {
        return questionTemplateService.filterByUser(token, keyword, isPublic, categoryId, templateType, sortType, sortBy, page, pageSize);
    }

    @GetMapping("/admin-filter")
    public ResponseEntity<?> filterByAdmin(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) QuestionTemplateStatus status,
                                           @RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false, defaultValue = "DESC") SortType sortType,
                                           @RequestParam(required = false, defaultValue = "id") String sortBy,
                                           @RequestParam(required = false,
                                                   defaultValue = PaginationConstant.PAGE_SIZE_DEFAULT_VALUE)
                                                   Integer pageSize,
                                           @RequestParam(required = false,
                                                   defaultValue = PaginationConstant.PAGE_DEFAULT_VALUE) Integer page) {
        return questionTemplateService.filterByAdmin(keyword, status, categoryId, sortType, sortBy, page,
                pageSize);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return questionTemplateService.getById(token, id);
    }

    @OnlyAdmin
    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approve(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return questionTemplateService.approve(token, id);
    }

    @OnlyAdmin
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> reject(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return questionTemplateService.reject(token, id);
    }

    @PostMapping("/share/{id}")
    public ResponseEntity<?> shareTemplate(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                           @PathVariable Long id) {
        return questionTemplateService.share(token, id);
    }

    @GetMapping("/explore")
    public ResponseEntity<?> explore(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Long categoryId,
                                     @RequestParam(required = false, defaultValue = "DESC") SortType sortType,
                                     @RequestParam(required = false, defaultValue = "id") String sortBy,
                                     @RequestParam(required = false, defaultValue =
                                             PaginationConstant.PAGE_SIZE_DEFAULT_VALUE) Integer pageSize,
                                     @RequestParam(required = false,
                                             defaultValue = PaginationConstant.PAGE_DEFAULT_VALUE) Integer page) {
        return questionTemplateService.explore(token, categoryId, keyword, sortType, sortBy, page, pageSize);
    }

    @PostMapping("/pull/{id}")
    public ResponseEntity<?> pullTemplate(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                          @PathVariable Long id) {
        return questionTemplateService.pullTemplate(token, id);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                          @RequestBody QuestionTemplateRequest request,
                                          @PathVariable Long id) {
        return questionTemplateService.updateStatus(token, id, request);
    }
}