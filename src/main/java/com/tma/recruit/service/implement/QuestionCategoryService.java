package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.QuestionCategory;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.mapper.QuestionCategoryMapper;
import com.tma.recruit.model.request.QuestionCategoryRequest;
import com.tma.recruit.repository.QuestionCategoryRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.IQuestionCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
public class QuestionCategoryService implements IQuestionCategoryService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private QuestionCategoryRepository questionCategoryRepository;

    @Autowired
    private QuestionCategoryMapper questionCategoryMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> create(String token, QuestionCategoryRequest request) {
        if (questionCategoryRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        User author = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory questionCategory = questionCategoryMapper.toEntity(request);
        questionCategory.setAuthor(author);
        questionCategory.setUpdatedUser(author);
        questionCategory = questionCategoryRepository.save(questionCategory);

        return ResponseEntity.status(HttpStatus.CREATED).body(questionCategoryMapper.toResponse(questionCategory));
    }

    @Override
    public ResponseEntity<?> update(String token, QuestionCategoryRequest request, Long id) {
        User updater = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory questionCategory = questionCategoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getName() != null && !questionCategory.getName().equals(request.getName())) {
            if (questionCategoryRepository.existsByNameAndActiveTrue(request.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }

        questionCategoryMapper.partialUpdate(questionCategory, request);
        questionCategory.setUpdatedUser(updater);
//        questionCategory.setUpdatedDate(new Date());
        questionCategory = questionCategoryRepository.save(questionCategory);

        return ResponseEntity.ok(questionCategoryMapper.toResponse(questionCategory));
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        User updater = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory questionCategory = questionCategoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//        questionCategory.setUpdatedDate(new Date());
        questionCategory.setUpdatedUser(updater);
        questionCategory.setActive(false);
        questionCategoryRepository.save(questionCategory);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(questionCategoryMapper.toResponse(questionCategoryRepository.findByActiveTrue()));
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        QuestionCategory questionCategory = questionCategoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(questionCategoryMapper.toResponse(questionCategory));
    }
}