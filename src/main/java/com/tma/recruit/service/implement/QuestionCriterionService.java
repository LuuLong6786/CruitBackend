package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.QuestionCriterion;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.QuestionStatus;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.mapper.QuestionCriterionMapper;
import com.tma.recruit.model.request.QuestionCriterionRequest;
import com.tma.recruit.model.request.UpdateEnableRequest;
import com.tma.recruit.model.response.ModelPage;
import com.tma.recruit.model.response.Pagination;
import com.tma.recruit.model.response.QuestionCriterionResponse;
import com.tma.recruit.repository.QuestionCriterionRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.IQuestionCriteriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        User author = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        QuestionCriterion criterion;
        if (questionCriterionRepository.existsByNameIgnoreCaseAndEnableTrue(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else if (questionCriterionRepository.existsByNameIgnoreCase(request.getName())) {
            criterion = questionCriterionRepository.findByNameIgnoreCase(request.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            criterion.setEnable(true);
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
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion criterion = questionCriterionRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getName() != null && !criterion.getName().equals(request.getName())) {
            if (questionCriterionRepository.existsByNameIgnoreCaseAndEnableTrue(request.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }

        questionCriterionMapper.partialUpdate(criterion, request);
        criterion.setUpdatedUser(updater);
        criterion.setUpdatedDate(new Date());
        criterion = questionCriterionRepository.save(criterion);

        return ResponseEntity.ok(questionCriterionMapper.toResponse(criterion));
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion criterion = questionCriterionRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        criterion.setUpdatedDate(new Date());
        criterion.setUpdatedUser(updater);
        criterion.setEnable(false);
        questionCriterionRepository.save(criterion);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAll(Boolean showDisabled) {
        return ResponseEntity.ok(questionCriterionMapper.toResponse(showDisabled ?
                questionCriterionRepository.findAll() :
                questionCriterionRepository.findByEnableTrue()));
    }

    @Override
    public ResponseEntity<?> getById(String token, Long id) {
        Optional<QuestionCriterion> criterion;
        if (jwtUtils.isAdmin(token)) {
            criterion = questionCriterionRepository.findById(id);
        } else {
            criterion = questionCriterionRepository.findByIdAndEnableTrue(id);
        }

        if (!criterion.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        QuestionCriterionResponse response = questionCriterionMapper.toResponse(criterion.get());
        response.setApprovedQuantity(getApprovedQuantity(criterion.get()));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> filter(String keyword, Boolean enable, Integer pageSize, Integer page, SortType sortType,
                                    String sortBy) {
        Pageable paging = PageRequest.of(page - 1, pageSize,
                SortType.DESC.equals(sortType) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Page<QuestionCriterion> criteria = questionCriterionRepository
                .filter(keyword, enable, paging);

        Pagination pagination = new Pagination(pageSize, page, criteria.getTotalPages(),
                criteria.getNumberOfElements());

        List<QuestionCriterionResponse> responses = questionCriterionMapper.toResponse(criteria.getContent());
        for (int i = 0; i < criteria.getContent().size(); i++) {
            responses.get(i).setApprovedQuantity(getApprovedQuantity(criteria.getContent().get(i)));
        }

        ModelPage<QuestionCriterionResponse> modelPage = new ModelPage<>(
                questionCriterionMapper.toResponse(criteria.getContent()), pagination);

        return ResponseEntity.ok(modelPage);
    }

    @Override
    public ResponseEntity<?> enable(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion criterion = questionCriterionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        criterion.setUpdatedDate(new Date());
        criterion.setUpdatedUser(updater);
        criterion.setEnable(true);
        questionCriterionRepository.save(criterion);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateEnable(String token, Long id, UpdateEnableRequest enable) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion criterion = questionCriterionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        criterion.setEnable(enable.getEnable());
        criterion.setUpdatedUser(updater);
        criterion.setUpdatedDate(new Date());
        criterion = questionCriterionRepository.save(criterion);

        return ResponseEntity.ok(questionCriterionMapper.toResponse(criterion));
    }

    private long getApprovedQuantity(QuestionCriterion criterion) {
        return criterion.getQuestions()
                .stream()
                .filter(q -> q.getEnable() && q.getStatus().equals(QuestionStatus.APPROVED))
                .count();
    }
}