package com.tma.recruit.controller;

import com.tma.recruit.anotation.OnlyAdmin;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.request.QuestionCategoryRequest;
import com.tma.recruit.model.request.UpdateActiveRequest;
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

    @OnlyAdmin
    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionCategoryRequest request) {
        return questionCategoryService.create(token, request);
    }

    @OnlyAdmin
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionCategoryRequest request,
                                    @PathVariable Long id) {
        return questionCategoryService.update(token, request, id);
    }

    @OnlyAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return questionCategoryService.delete(id);
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestParam(required = false, defaultValue = "false") Boolean showDisabled) {
        return questionCategoryService.getAll(showDisabled);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) Boolean active,
                                    @RequestParam(required = false, defaultValue = "DESC") SortType sortType,
                                    @RequestParam(required = false, defaultValue = "id") String sortBy,
                                    @RequestParam(required = false,
                                            defaultValue = PaginationConstant.PAGE_SIZE_DEFAULT_VALUE) Integer pageSize,
                                    @RequestParam(required = false,
                                            defaultValue = PaginationConstant.PAGE_DEFAULT_VALUE) Integer page) {
        return questionCategoryService.filter(keyword, active, pageSize, page, sortType, sortBy);
    }

    @OnlyAdmin
    @PutMapping("/active/{id}")
    public ResponseEntity<?> active(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return questionCategoryService.active(token, id);
    }

    @OnlyAdmin
    @DeleteMapping("/inactive/{id}")
    public ResponseEntity<?> inactive(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return questionCategoryService.inactive(token, id);
    }

    @OnlyAdmin
    @PutMapping("/update-active/{id}")
    public ResponseEntity<?> updateActive(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                          @RequestBody UpdateActiveRequest active,
                                          @PathVariable Long id) {
        return questionCategoryService.updateActive(token, id, active);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return questionCategoryService.getById(token, id);
    }
}
