package com.tma.recruit.controller;

import com.tma.recruit.anotation.OnlyAdmin;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.request.QuestionCriterionRequest;
import com.tma.recruit.service.interfaces.IQuestionCriteriaService;
import com.tma.recruit.util.Constant;
import com.tma.recruit.util.PaginationConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question-criteria")
public class QuestionCriteriaController {

    @Autowired
    private IQuestionCriteriaService questionCriteriaService;

    @OnlyAdmin
    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionCriterionRequest request) {
        return questionCriteriaService.create(token, request);
    }

    @OnlyAdmin
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionCriterionRequest request,
                                    @PathVariable Long id) {
        return questionCriteriaService.update(token, request, id);
    }

    @OnlyAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return questionCriteriaService.delete(token, id);
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestParam(required = false, defaultValue = "false") Boolean showDisabled) {
        return questionCriteriaService.getAll(showDisabled);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) Boolean enable,
                                    @RequestParam(required = false, defaultValue = "DESC") SortType sortType,
                                    @RequestParam(required = false, defaultValue = "id") String sortBy,
                                    @RequestParam(required = false,
                                            defaultValue = PaginationConstant.PAGE_SIZE_DEFAULT_VALUE) Integer pageSize,
                                    @RequestParam(required = false,
                                            defaultValue = PaginationConstant.PAGE_DEFAULT_VALUE) Integer page) {
        return questionCriteriaService.filter(keyword, enable, pageSize, page, sortType, sortBy);
    }

    @OnlyAdmin
    @PutMapping("/enable/{id}")
    public ResponseEntity<?> enable(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return questionCriteriaService.enable(token, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return questionCriteriaService.getById(token, id);
    }
}
