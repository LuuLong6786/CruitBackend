package com.tma.recruit.controller;

import com.tma.recruit.model.enums.QuestionLevelEnum;
import com.tma.recruit.model.request.QuestionBankRequest;
import com.tma.recruit.service.interfaces.IQuestionBankService;
import com.tma.recruit.util.Constant;
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

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return questionBankService.approve(token, id);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return questionBankService.reject(token, id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionBankRequest request,
                                    @PathVariable Long id) {
        return questionBankService.update(token, request, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return questionBankService.delete(token, id);
    }

    @GetMapping
    public ResponseEntity<?> getApprovedQuestion(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return questionBankService.getApprovedQuestion();
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return questionBankService.getAll();
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestParam(required = false) QuestionLevelEnum level,
                                    @RequestParam(required = false) Long categoryId,
                                    @RequestParam(required = false) Long criterionId,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                                    @RequestParam(required = false, defaultValue = "1") Integer page) {
        return questionBankService.filter(level, categoryId, criterionId, pageSize, page, keyword);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return questionBankService.getById(id);
    }
}