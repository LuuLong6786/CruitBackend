package com.tma.recruit.controller;

import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.request.QuestionCategoryRequest;
import com.tma.recruit.service.interfaces.IQuestionCategoryService;
import com.tma.recruit.util.Constant;
import com.tma.recruit.util.PaginationConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question-category")
public class QuestionCategoryController {

    @Autowired
    private IQuestionCategoryService questionCategoryService;

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionCategoryRequest request) {
        return questionCategoryService.create(token, request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionCategoryRequest request,
                                    @PathVariable Long id) {
        return questionCategoryService.update(token, request, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return questionCategoryService.delete(token, id);
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return questionCategoryService.getAll();
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false, defaultValue = "true") Boolean enable,
                                    @RequestParam(required = false, defaultValue = "DESC") SortType sortType,
                                    @RequestParam(required = false, defaultValue = "id") String sortBy,
                                    @RequestParam(required = false,
                                            defaultValue = PaginationConstant.PAGE_SIZE_DEFAULT_VALUE) Integer pageSize,
                                    @RequestParam(required = false,
                                            defaultValue = PaginationConstant.PAGE_DEFAULT_VALUE) Integer page) {
        return questionCategoryService.filter(keyword, enable, pageSize, page, sortType, sortBy);
    }

    @GetMapping("/enable/{id}")
    public ResponseEntity<?> enable(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return questionCategoryService.enable(token, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return questionCategoryService.getById(id);
    }
}
