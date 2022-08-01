package com.tma.recruit.controller;

import com.tma.recruit.anotation.OnlyAdmin;
import com.tma.recruit.model.enums.QuestionLevel;
import com.tma.recruit.model.enums.QuestionStatus;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.request.QuestionBankRequest;
import com.tma.recruit.service.interfaces.IQuestionBankService;
import com.tma.recruit.util.Constant;
import com.tma.recruit.util.PaginationConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
public class QuestionBankController {

    @Autowired
    private IQuestionBankService questionBankService;

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionBankRequest request) {
        return questionBankService.create(token, request);
    }

    @OnlyAdmin
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return questionBankService.approve(token, id);
    }

    @OnlyAdmin
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return questionBankService.reject(token, id);
    }

    @OnlyAdmin
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionBankRequest request,
                                    @PathVariable Long id) {
        return questionBankService.update(token, request, id);
    }

    @OnlyAdmin
    @DeleteMapping("/inactive/{id}")
    public ResponseEntity<?> inactive(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return questionBankService.inactive(token, id);
    }

    @OnlyAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                      @PathVariable Long id) {
        return questionBankService.delete( id);
    }

    @GetMapping
    public ResponseEntity<?> getApprovedQuestion(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return questionBankService.getApprovedQuestion();
    }

    @OnlyAdmin
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return questionBankService.getAll();
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestParam(required = false) QuestionLevel level,
                                    @RequestParam(required = false) Long categoryId,
                                    @RequestParam(required = false) Long criterionId,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false, defaultValue = "APPROVED") QuestionStatus status,
                                    @RequestParam(required = false, defaultValue = "DESC") SortType sortType,
                                    @RequestParam(required = false, defaultValue = "id") String sortBy,
                                    @RequestParam(required = false,
                                            defaultValue = PaginationConstant.PAGE_SIZE_DEFAULT_VALUE) Integer pageSize,
                                    @RequestParam(required = false,
                                            defaultValue = PaginationConstant.PAGE_DEFAULT_VALUE) Integer page) {
        return questionBankService.filter(token, status, level, categoryId, criterionId, pageSize, page, keyword,
                sortType, sortBy);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return questionBankService.getById(token, id);
    }
}