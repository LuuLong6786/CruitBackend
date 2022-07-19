package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.QuestionCriterion;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.QuestionStatus;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.mapper.QuestionCriterionMapper;
import com.tma.recruit.model.request.QuestionCriterionRequest;
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

        QuestionCriterion questionCriterion;
        if (questionCriterionRepository.existsByNameIgnoreCaseAndEnableTrue(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else if (questionCriterionRepository.existsByNameIgnoreCase(request.getName())) {
            questionCriterion = questionCriterionRepository.findByNameIgnoreCase(request.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            questionCriterion.setEnable(true);
            questionCriterion.setUpdatedDate(new Date());
        } else {
            questionCriterion = questionCriterionMapper.toEntity(request);
            questionCriterion.setAuthor(author);
        }
        questionCriterion.setUpdatedUser(author);
        questionCriterion = questionCriterionRepository.save(questionCriterion);

        return ResponseEntity.status(HttpStatus.CREATED).body(questionCriterionMapper.toResponse(questionCriterion));
    }

    @Override
    public ResponseEntity<?> update(String token, QuestionCriterionRequest request, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion questionCriterion = questionCriterionRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getName() != null && !questionCriterion.getName().equals(request.getName())) {
            if (questionCriterionRepository.existsByNameIgnoreCaseAndEnableTrue(request.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }

        questionCriterionMapper.partialUpdate(questionCriterion, request);
        questionCriterion.setUpdatedUser(updater);
        questionCriterion.setUpdatedDate(new Date());
        questionCriterion = questionCriterionRepository.save(questionCriterion);

        return ResponseEntity.ok(questionCriterionMapper.toResponse(questionCriterion));
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion questionCriterion = questionCriterionRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        questionCriterion.setUpdatedDate(new Date());
        questionCriterion.setUpdatedUser(updater);
        questionCriterion.setEnable(false);
        questionCriterionRepository.save(questionCriterion);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAll(Boolean showDisabled) {
        return ResponseEntity.ok(questionCriterionMapper.toResponse(showDisabled ?
                questionCriterionRepository.findAll() :
                questionCriterionRepository.findByEnableTrue()));
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        QuestionCriterion questionCriterion = questionCriterionRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        long approvedQuantity = questionCriterion.getQuestions()
                .stream()
                .filter(q -> q.getEnable() && q.getStatus().equals(QuestionStatus.APPROVED))
                .count();

        QuestionCriterionResponse response = questionCriterionMapper.toResponse(questionCriterion);
        response.setApprovedQuantity(approvedQuantity);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> filter(String keyword, Boolean enable, Integer pageSize, Integer page, SortType sortType,
                                    String sortBy) {
        Pageable paging = PageRequest.of(page - 1, pageSize,
                SortType.DESC.equals(sortType) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Page<QuestionCriterion> questionCriteria = questionCriterionRepository
                .filter(keyword, enable, paging);

        Pagination pagination = new Pagination(pageSize, page, questionCriteria.getTotalPages(),
                questionCriteria.getNumberOfElements());

        ModelPage<QuestionCriterionResponse> modelPage = new ModelPage<>(
                questionCriterionMapper.toResponse(questionCriteria.getContent()), pagination);

        return ResponseEntity.ok(modelPage);
    }

    @Override
    public ResponseEntity<?> enable(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCriterion questionCriterion = questionCriterionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        questionCriterion.setUpdatedDate(new Date());
        questionCriterion.setUpdatedUser(updater);
        questionCriterion.setEnable(true);
        questionCriterionRepository.save(questionCriterion);

        return ResponseEntity.ok(HttpStatus.OK);
    }
}