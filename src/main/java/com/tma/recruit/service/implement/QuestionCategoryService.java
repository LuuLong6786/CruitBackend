package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.QuestionCategory;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.QuestionStatus;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.mapper.QuestionCategoryMapper;
import com.tma.recruit.model.request.QuestionCategoryRequest;
import com.tma.recruit.model.request.UpdateActiveRequest;
import com.tma.recruit.model.response.ModelPage;
import com.tma.recruit.model.response.Pagination;
import com.tma.recruit.model.response.QuestionCategoryResponse;
import com.tma.recruit.repository.QuestionCategoryRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.IQuestionCategoryService;
import com.tma.recruit.util.PaginationConstant;
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
        User author = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        QuestionCategory category;
        if (questionCategoryRepository.existsByNameIgnoreCaseAndActiveTrue(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else if (questionCategoryRepository.existsByNameIgnoreCase(request.getName())) {
            category = questionCategoryRepository.findByNameIgnoreCase(request.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            category.setActive(true);
            category.setUpdatedDate(new Date());
        } else {
            category = questionCategoryMapper.toEntity(request);
            category.setAuthor(author);
        }
        category.setUpdatedUser(author);
        category = questionCategoryRepository.save(category);

        return ResponseEntity.status(HttpStatus.CREATED).body(questionCategoryMapper.toResponse(category));
    }

    @Override
    public ResponseEntity<?> update(String token, QuestionCategoryRequest request, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory category = questionCategoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (modifiable(category)) {
            if (request.getName() != null && !category.getName().equals(request.getName())) {
                if (questionCategoryRepository.existsByNameIgnoreCaseAndActiveTrue(request.getName())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT);
                }
            }
            questionCategoryMapper.partialUpdate(category, request);
            category.setUpdatedUser(updater);
            category.setUpdatedDate(new Date());
            category = questionCategoryRepository.save(category);

            return ResponseEntity.ok(questionCategoryMapper.toResponse(category));
        } else {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @Override
    public ResponseEntity<?> inactive(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        QuestionCategory category = questionCategoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        category.setUpdatedDate(new Date());
        category.setUpdatedUser(updater);
        category.setActive(false);
        questionCategoryRepository.save(category);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAll(Boolean showDisabled) {
        List<QuestionCategory> categories = showDisabled ?
                questionCategoryRepository.findAll() :
                questionCategoryRepository.findByActiveTrue();

        List<QuestionCategoryResponse> responses = questionCategoryMapper.toResponse(categories);
        for (int i = 0; i < categories.size(); i++) {
            setPendingAndApprovedQuantity(categories.get(i), responses.get(i));
        }

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<?> getById(String token, Long id) {
        Optional<QuestionCategory> category;
        if (jwtUtils.isAdmin(token)) {
            category = questionCategoryRepository.findById(id);
        } else {
            category = questionCategoryRepository.findByIdAndActiveTrue(id);
        }

        if (!category.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        QuestionCategoryResponse response = questionCategoryMapper.toResponse(category.get());
        setPendingAndApprovedQuantity(category.get(), response);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> filter(String keyword, Boolean active, Integer pageSize, Integer page, SortType sortType,
                                    String sortBy) {
        Pageable paging = PageRequest.of(PaginationConstant.getPage(page), pageSize,
                SortType.DESC.equals(sortType) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Page<QuestionCategory> categories = questionCategoryRepository.filter(keyword, active, paging);

        Pagination pagination = new Pagination(pageSize, page, categories.getTotalPages(),
                categories.getTotalElements());

        List<QuestionCategoryResponse> categoryResponses = questionCategoryMapper.toResponse(categories.getContent());
        for (int i = 0; i < categories.getContent().size(); i++) {
            setPendingAndApprovedQuantity(categories.getContent().get(i), categoryResponses.get(i));
        }

        ModelPage<QuestionCategoryResponse> modelPage = new ModelPage<>(categoryResponses, pagination);

        return ResponseEntity.ok(modelPage);
    }

    @Override
    public ResponseEntity<?> active(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory category = questionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        category.setUpdatedDate(new Date());
        category.setUpdatedUser(updater);
        category.setActive(true);
        questionCategoryRepository.save(category);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateActive(String token, Long id, UpdateActiveRequest active) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory category = questionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        category.setActive(active.getActive());
        category.setUpdatedUser(updater);
        category.setUpdatedDate(new Date());
        category = questionCategoryRepository.save(category);

        return ResponseEntity.ok(questionCategoryMapper.toResponse(category));
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        QuestionCategory category = questionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (modifiable(category)) {
            questionCategoryRepository.delete(category);

            return ResponseEntity.ok(HttpStatus.OK);
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(HttpStatus.METHOD_NOT_ALLOWED);
    }

    private boolean modifiable(QuestionCategory category) {
        return category.getQuestions().isEmpty();
    }

    private void setPendingAndApprovedQuantity(QuestionCategory questionCategory,
                                               QuestionCategoryResponse questionCategoryResponse) {
        questionCategoryResponse.setApprovedQuantity(getApprovedQuantity(questionCategory));
        questionCategoryResponse.setPendingQuantity(getPendingQuantity(questionCategory));
    }

    private long getApprovedQuantity(QuestionCategory questionCategory) {
        return questionCategory.getQuestions()
                .stream()
                .filter(q -> q.getStatus().equals(QuestionStatus.APPROVED))
                .count();
    }

    private long getPendingQuantity(QuestionCategory questionCategory) {
        return questionCategory.getQuestions()
                .stream()
                .filter(q -> q.getStatus().equals(QuestionStatus.PENDING))
                .count();
    }
}