package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.QuestionCriterion;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.QuestionStatus;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.mapper.QuestionCriterionMapper;
import com.tma.recruit.model.request.QuestionCriterionRequest;
import com.tma.recruit.model.request.UpdateActiveRequest;
import com.tma.recruit.model.response.ModelPage;
import com.tma.recruit.model.response.Pagination;
import com.tma.recruit.model.response.QuestionCriterionResponse;
import com.tma.recruit.repository.QuestionCriterionRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.IQuestionCriteriaService;
import com.tma.recruit.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
        User author = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        QuestionCriterion criterion;
        if (questionCriterionRepository.existsByNameIgnoreCaseAndActiveTrue(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else if (questionCriterionRepository.existsByNameIgnoreCase(request.getName())) {
            criterion = questionCriterionRepository.findByNameIgnoreCase(request.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            criterion.setActive(true);
            criterion.setUpdatedDate(new Date());
        } else {
            criterion = questionCriterionMapper.toEntity(request);
            criterion.setAuthor(author);
        }
        criterion.setUpdatedUser(author);
        criterion = questionCriterionRepository.save(criterion);

        return ResponseEntity.status(HttpStatus.CREATED).body(questionCriterionMapper.toResponse(criterion));
    }

    @Override
    public ResponseEntity<?> update(String token, QuestionCriterionRequest request, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion criterion = questionCriterionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (modifiable(criterion)) {
            if (request.getName() != null && !criterion.getName().equals(request.getName())) {
                if (questionCriterionRepository.existsByNameIgnoreCaseAndActiveTrue(request.getName())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT);
                }
            }

            questionCriterionMapper.partialUpdate(criterion, request);
            criterion.setUpdatedUser(updater);
            criterion.setUpdatedDate(new Date());
            criterion = questionCriterionRepository.save(criterion);

            return ResponseEntity.ok(questionCriterionMapper.toResponse(criterion));
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseEntity<?> inactive(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion criterion = questionCriterionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        criterion.setUpdatedDate(new Date());
        criterion.setUpdatedUser(updater);
        criterion.setActive(false);
        questionCriterionRepository.save(criterion);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAll(Boolean showDisabled) {
        List<QuestionCriterion> criteria = showDisabled ?
                questionCriterionRepository.findAll() :
                questionCriterionRepository.findByActiveTrue();

        List<QuestionCriterionResponse> responses = questionCriterionMapper.toResponse(criteria);
        for (int i = 0; i < criteria.size(); i++) {
            setPendingAndApprovedQuantity(criteria.get(i), responses.get(i));
        }

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<?> getById(String token, Long id) {
        Optional<QuestionCriterion> criterion;
        if (jwtUtils.isAdmin(token)) {
            criterion = questionCriterionRepository.findById(id);
        } else {
            criterion = questionCriterionRepository.findByIdAndActiveTrue(id);
        }

        if (!criterion.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        QuestionCriterionResponse response = questionCriterionMapper.toResponse(criterion.get());
        setPendingAndApprovedQuantity(criterion.get(), response);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> filter(String keyword, Boolean active, Integer pageSize, Integer page, SortType sortType,
                                    String sortBy) {
        PaginationUtil paginationUtil = PaginationUtil.builder()
                .page(page)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortType(sortType)
                .build();
        Pageable paging = paginationUtil.getPageable();
        Page<QuestionCriterion> criteria = questionCriterionRepository.filter(keyword, active, paging);
        Pagination pagination = paginationUtil.getPagination(criteria);

        List<QuestionCriterionResponse> responses = questionCriterionMapper.toResponse(criteria.getContent());
        for (int i = 0; i < criteria.getContent().size(); i++) {
            setPendingAndApprovedQuantity(criteria.getContent().get(i), responses.get(i));
        }

        ModelPage<QuestionCriterionResponse> modelPage = new ModelPage<>(responses, pagination);

        return ResponseEntity.ok(modelPage);
    }

    @Override
    public ResponseEntity<?> active(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion criterion = questionCriterionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        criterion.setUpdatedDate(new Date());
        criterion.setUpdatedUser(updater);
        criterion.setActive(true);
        questionCriterionRepository.save(criterion);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateActive(String token, Long id, UpdateActiveRequest active) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion criterion = questionCriterionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        criterion.setActive(active.getActive());
        criterion.setUpdatedUser(updater);
        criterion.setUpdatedDate(new Date());
        criterion = questionCriterionRepository.save(criterion);

        return ResponseEntity.ok(questionCriterionMapper.toResponse(criterion));
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        QuestionCriterion criterion = questionCriterionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (modifiable(criterion)) {
            questionCriterionRepository.delete(criterion);

            return ResponseEntity.ok(HttpStatus.OK);
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(HttpStatus.METHOD_NOT_ALLOWED);
    }

    private boolean modifiable(QuestionCriterion criterion) {
        return criterion.getQuestions().isEmpty();
    }

    private void setPendingAndApprovedQuantity(QuestionCriterion criterion,
                                               QuestionCriterionResponse criterionResponse) {
        criterionResponse.setApprovedQuantity(getApprovedQuantity(criterion));
        criterionResponse.setPendingQuantity(getPendingQuantity(criterion));
    }

    private long getApprovedQuantity(QuestionCriterion criterion) {
        return criterion.getQuestions()
                .stream()
                .filter(q -> q.getStatus().equals(QuestionStatus.APPROVED))
                .count();
    }

    private long getPendingQuantity(QuestionCriterion criterion) {
        return criterion.getQuestions()
                .stream()
                .filter(q -> q.getStatus().equals(QuestionStatus.PENDING))
                .count();
    }
}