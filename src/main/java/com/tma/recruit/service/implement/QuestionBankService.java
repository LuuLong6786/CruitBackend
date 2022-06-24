package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.QuestionBank;
import com.tma.recruit.model.entity.QuestionCategory;
import com.tma.recruit.model.entity.QuestionCriterion;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.QuestionLevelEnum;
import com.tma.recruit.model.mapper.QuestionBankMapper;
import com.tma.recruit.model.request.QuestionBankRequest;
import com.tma.recruit.model.request.QuestionCriterionRequest;
import com.tma.recruit.model.response.ModelPage;
import com.tma.recruit.model.response.Pagination;
import com.tma.recruit.model.response.QuestionBankResponse;
import com.tma.recruit.model.response.UserResponse;
import com.tma.recruit.repository.QuestionBankRepository;
import com.tma.recruit.repository.QuestionCategoryRepository;
import com.tma.recruit.repository.QuestionCriterionRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.IQuestionBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuestionBankService implements IQuestionBankService {

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionCategoryRepository questionCategoryRepository;

    @Autowired
    private QuestionCriterionRepository questionCriterionRepository;

    @Override
    public ResponseEntity<?> create(String token, QuestionBankRequest request) {
        User author = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory questionCategory = questionCategoryRepository
                .findByIdAndActiveTrue(request.getCategory().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<QuestionCriterion> questionCriteria = new ArrayList<>();
        for (QuestionCriterionRequest criterionRequest : request.getCriteria()) {
            QuestionCriterion criterion = questionCriterionRepository.findByIdAndActiveTrue(criterionRequest.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            questionCriteria.add(criterion);
        }
        QuestionBank questionBank = questionBankMapper.toEntity(request);
        questionBank.setCategory(questionCategory);
        questionBank.setUpdatedUser(author);
        questionBank.setAuthor(author);
        questionBank.setCriteria(questionCriteria);
        questionBank = questionBankRepository.save(questionBank);

        return ResponseEntity.status(HttpStatus.CREATED).body(questionBankMapper.toResponse(questionBank));
    }

    @Override
    public ResponseEntity<?> update(String token, QuestionBankRequest request, Long id) {
        User updater = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionBank questionBank = questionBankRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        questionBankMapper.partialUpdate(questionBank, request);

        if (request.getCriteria() != null && request.getCriteria().size() > 0) {
            List<QuestionCriterion> criteria = new ArrayList<>();
            for (QuestionCriterionRequest questionCriterionRequest : request.getCriteria()) {
                if (questionCriterionRequest.getId() > 0) {
                    QuestionCriterion criterion = questionCriterionRepository.findByIdAndActiveTrue(questionCriterionRequest.getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                    criteria.add(criterion);
                }
            }
            questionBank.setCriteria(criteria);
        }
        if (request.getCategory() != null && request.getCategory().getId() > 0) {
            QuestionCategory questionCategory = questionCategoryRepository
                    .findByIdAndActiveTrue(request.getCategory().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            questionBank.setCategory(questionCategory);
        }
        questionBank.setUpdatedUser(updater);
//        questionBank.setUpdatedDate(new Date());
        questionBank = questionBankRepository.save(questionBank);

        return ResponseEntity.ok(questionBankMapper.toResponse(questionBank));
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        User updater = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionBank questionBank = questionBankRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//        questionBank.setUpdatedDate(new Date());
        questionBank.setUpdatedUser(updater);
        questionBank.setActive(false);
        questionBankRepository.save(questionBank);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAll() {
        List<QuestionBank> questionBanks = questionBankRepository.findByActiveTrue();

        return ResponseEntity.ok(questionBankMapper.toResponse(questionBankRepository.findByActiveTrue()));
    }

    @Override
    public ResponseEntity<?> getApprovedQuestion() {
        List<QuestionBank> questionBanks = questionBankRepository.findByApprovedTrueAndActiveTrue();

        return ResponseEntity.ok(questionBankMapper.toResponse(questionBanks));
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        QuestionBank questionBank = questionBankRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(questionBankMapper.toResponse(questionBank));
    }

    @Override
    public ResponseEntity<?> approve(String token, Long id) {
        User approver = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionBank questionBank = questionBankRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        questionBank.setApprover(approver);
        questionBank.setApprovedDate(new Date());
        questionBank.setApproved(true);
        questionBankRepository.save(questionBank);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> reject(String token, Long id) {
        User approver = userRepository.findByEmailIgnoreCaseAndActiveTrue(jwtUtils.getEmailFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionBank questionBank = questionBankRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        questionBank.setApprover(approver);
        questionBank.setApprovedDate(new Date());
        questionBank.setApproved(false);
        questionBankRepository.save(questionBank);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> filter(QuestionLevelEnum level, Long categoryId, Long criterionId, Integer pageSize, Integer page) {
        Pageable paging = PageRequest.of(page - 1, pageSize);

        Page<QuestionBank> questionBanks = questionBankRepository.filter(level, categoryId,criterionId, paging);

        Pagination pagination = new Pagination(pageSize, page, questionBanks.getTotalPages(),
                questionBanks.getNumberOfElements());

        ModelPage<QuestionBankResponse> modelPage = new ModelPage<>(
                questionBankMapper.toResponse(questionBanks.getContent()), pagination);

        return ResponseEntity.ok(modelPage);
    }
}