package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.QuestionCriterion;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.mapper.QuestionCriterionMapper;
import com.tma.recruit.model.request.QuestionCriterionRequest;
import com.tma.recruit.repository.QuestionCriterionRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.IQuestionCriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
public class QuestionCriterionService implements IQuestionCriteriaService {

    @Autowired
    private QuestionCriterionRepository questionCriterionRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private QuestionCriterionMapper questionCriterionMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> create(String token, QuestionCriterionRequest request) {
        User author = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (questionCriterionRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        QuestionCriterion questionCriterion = questionCriterionMapper.toEntity(request);
        questionCriterion.setAuthor(author);
        questionCriterion.setUpdatedUser(author);
        questionCriterion = questionCriterionRepository.save(questionCriterion);

        return ResponseEntity.status(HttpStatus.CREATED).body(questionCriterionMapper.toResponse(questionCriterion));
    }

    @Override
    public ResponseEntity<?> update(String token, QuestionCriterionRequest request, Long id) {
        User updater = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion questionCriterion = questionCriterionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getName() != null && !questionCriterion.getName().equals(request.getName())) {
            if (questionCriterionRepository.existsByNameAndActiveTrue(request.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }

        questionCriterionMapper.partialUpdate(questionCriterion, request);
        questionCriterion.setUpdatedUser(updater);
//        questionCriterion.setUpdatedDate(new Date());
        questionCriterion = questionCriterionRepository.save(questionCriterion);

        return ResponseEntity.ok(questionCriterionMapper.toResponse(questionCriterion));
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        User updater = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion questionCriterion = questionCriterionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//        questionCriterion.setUpdatedDate(new Date());
        questionCriterion.setUpdatedUser(updater);
        questionCriterion.setActive(false);
        questionCriterionRepository.save(questionCriterion);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(questionCriterionMapper.toResponse(questionCriterionRepository.findByActiveTrue()));
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        QuestionCriterion questionCriterion = questionCriterionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(questionCriterionMapper.toResponse(questionCriterion));
    }
}