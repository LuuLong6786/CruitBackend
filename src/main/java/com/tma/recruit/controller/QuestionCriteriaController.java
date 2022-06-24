package com.tma.recruit.controller;

import com.tma.recruit.model.request.QuestionCriterionRequest;
import com.tma.recruit.service.interfaces.IQuestionCriteriaService;
import com.tma.recruit.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question-criteria")
public class QuestionCriteriaController {

    @Autowired
    private IQuestionCriteriaService questionCriteriaService;

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionCriterionRequest request) {
        return questionCriteriaService.create(token, request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @RequestBody QuestionCriterionRequest request,
                                    @PathVariable Long id) {
        return questionCriteriaService.update(token, request, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                    @PathVariable Long id) {
        return questionCriteriaService.delete(token, id);
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return questionCriteriaService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return questionCriteriaService.getById(id);
    }
}
