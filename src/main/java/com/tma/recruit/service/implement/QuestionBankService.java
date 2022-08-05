package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.QuestionBank;
import com.tma.recruit.model.entity.QuestionCategory;
import com.tma.recruit.model.entity.QuestionCriterion;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.QuestionLevel;
import com.tma.recruit.model.enums.QuestionStatus;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.mapper.QuestionBankMapper;
import com.tma.recruit.model.request.QuestionBankRequest;
import com.tma.recruit.model.request.QuestionCriterionRequest;
import com.tma.recruit.model.response.ModelPage;
import com.tma.recruit.model.response.Pagination;
import com.tma.recruit.model.response.QuestionBankResponse;
import com.tma.recruit.repository.QuestionBankRepository;
import com.tma.recruit.repository.QuestionCategoryRepository;
import com.tma.recruit.repository.QuestionCriterionRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.INotificationService;
import com.tma.recruit.service.interfaces.IQuestionBankService;
import com.tma.recruit.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
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

    @Autowired
    private INotificationService notificationService;

    @Override
    public ResponseEntity<?> create(String token, QuestionBankRequest request) {
        User author = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
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

        notificationService.notifyCreationToAdmin(questionBank);

        return ResponseEntity.status(HttpStatus.CREATED).body(questionBankMapper.toResponse(questionBank));
    }

    @Override
    public ResponseEntity<?> update(String token, QuestionBankRequest request, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionBank questionBank = questionBankRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        questionBankMapper.partialUpdate(questionBank, request);

        if (request.getCriteria() != null && request.getCriteria().size() > 0) {
            List<QuestionCriterion> criteria = new ArrayList<>();
            for (QuestionCriterionRequest questionCriterionRequest : request.getCriteria()) {
                if (questionCriterionRequest.getId() != null && questionCriterionRequest.getId() > 0) {
                    QuestionCriterion criterion = questionCriterionRepository
                            .findByIdAndActiveTrue(questionCriterionRequest.getId())
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
        questionBank.setUpdatedDate(new Date());
        questionBank = questionBankRepository.save(questionBank);

        notificationService.notifyUpdateToAdmin(questionBank);

        return ResponseEntity.ok(questionBankMapper.toResponse(questionBank));
    }

    @Override
    public ResponseEntity<?> inactive(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionBank questionBank = questionBankRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        questionBank.setUpdatedDate(new Date());
        questionBank.setUpdatedUser(updater);
        questionBank.setActive(false);
        questionBankRepository.save(questionBank);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(questionBankMapper.toResponse(questionBankRepository.findByActiveTrue()));
    }

    @Override
    public ResponseEntity<?> getApprovedQuestion() {
        List<QuestionBank> questionBanks = questionBankRepository.findByActiveTrue();

        return ResponseEntity.ok(questionBankMapper.toResponse(questionBanks));
    }

    @Override
    public ResponseEntity<?> getById(String token, Long id) {
        Optional<QuestionBank> question;
        if (jwtUtils.isAdmin(token)) {
            question = questionBankRepository.findById(id);
        } else {
            question = questionBankRepository.findByIdAndActiveTrue(id);
        }

        if (!question.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(questionBankMapper.toResponse(question.get()));
    }

    @Override
    public ResponseEntity<?> approve(String token, Long id) {
        User approver = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionBank questionBank = questionBankRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        questionBank.setApprover(approver);
        questionBank.setApprovedDate(new Date());
        questionBank.setStatus(QuestionStatus.APPROVED);
        questionBank = questionBankRepository.save(questionBank);
        notificationService.notifyUpdateToAdmin(questionBank);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> reject(String token, Long id) {
        User approver = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionBank questionBank = questionBankRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        questionBank.setApprover(approver);
        questionBank.setApprovedDate(new Date());
        questionBank.setStatus(QuestionStatus.REJECTED);
        questionBankRepository.save(questionBank);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> filter(String token, QuestionStatus status, QuestionLevel level, Long categoryId,
                                    Long criterionId, Integer pageSize, Integer page, String keyword, SortType sortType,
                                    String sortBy) {
        if (!QuestionStatus.APPROVED.equals(status)) {
            if (!jwtUtils.isAdmin(token)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }
        
        PaginationUtil paginationUtil = PaginationUtil.builder()
                .page(page)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortType(sortType)
                .build();
        Pageable paging = paginationUtil.getPageable();
        Page<QuestionBank> questionBanks = questionBankRepository
                .filter(level != null ? level.toString() : null, categoryId, criterionId, keyword,
                        status != null ? status.toString() : null, paging);
        Pagination pagination = paginationUtil.getPagination(questionBanks);

        ModelPage<QuestionBankResponse> modelPage = new ModelPage<>(
                questionBankMapper.toResponse(questionBanks.getContent()), pagination);

        return ResponseEntity.ok(modelPage);
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        return null;
    }
}