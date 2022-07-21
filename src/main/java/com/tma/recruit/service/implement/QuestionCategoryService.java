package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.QuestionCategory;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.QuestionStatus;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.mapper.QuestionCategoryMapper;
import com.tma.recruit.model.request.QuestionCategoryRequest;
import com.tma.recruit.model.request.UpdateEnableRequest;
import com.tma.recruit.model.response.ModelPage;
import com.tma.recruit.model.response.Pagination;
import com.tma.recruit.model.response.QuestionCategoryResponse;
import com.tma.recruit.repository.QuestionCategoryRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.IQuestionCategoryService;
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
        User author = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        QuestionCategory category;
        if (questionCategoryRepository.existsByNameIgnoreCaseAndEnableTrue(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else if (questionCategoryRepository.existsByNameIgnoreCase(request.getName())) {
            category = questionCategoryRepository.findByNameIgnoreCase(request.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            category.setEnable(true);
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
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory category = questionCategoryRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getName() != null && !category.getName().equals(request.getName())) {
            if (questionCategoryRepository.existsByNameIgnoreCaseAndEnableTrue(request.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }

        questionCategoryMapper.partialUpdate(category, request);
        category.setUpdatedUser(updater);
        category.setUpdatedDate(new Date());
        category = questionCategoryRepository.save(category);

        return ResponseEntity.ok(questionCategoryMapper.toResponse(category));
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory category = questionCategoryRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        category.setUpdatedDate(new Date());
        category.setUpdatedUser(updater);
        category.setEnable(false);
        questionCategoryRepository.save(category);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAll(Boolean showDisabled) {
        List<QuestionCategory> categories = showDisabled ?
                questionCategoryRepository.findAll() :
                questionCategoryRepository.findByEnableTrue();

        List<QuestionCategoryResponse> responses = questionCategoryMapper.toResponse(categories);
        for (int i = 0; i < categories.size(); i++) {
            responses.get(i).setApprovedQuantity(getApprovedQuantity(categories.get(i)));
        }

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<?> getById(String token, Long id) {
        Optional<QuestionCategory> category;
        if (jwtUtils.isAdmin(token)){
            category = questionCategoryRepository.findById(id);
        }else {
            category = questionCategoryRepository.findByIdAndEnableTrue(id);
        }

        if (!category.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        QuestionCategoryResponse response = questionCategoryMapper.toResponse(category.get());
        response.setApprovedQuantity(getApprovedQuantity(category.get()));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> filter(String keyword, Boolean enable, Integer pageSize, Integer page, SortType sortType,
                                    String sortBy) {
        Pageable paging = PageRequest.of(page - 1, pageSize,
                SortType.DESC.equals(sortType) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Page<QuestionCategory> categories = questionCategoryRepository
                .filter(keyword, enable, paging);

        Pagination pagination = new Pagination(pageSize, page, categories.getTotalPages(),
                categories.getNumberOfElements());

        List<QuestionCategoryResponse> categoryResponses = questionCategoryMapper.toResponse(categories.getContent());
        for (int i = 0; i < categories.getContent().size(); i++) {
            categoryResponses.get(i).setApprovedQuantity(getApprovedQuantity(categories.getContent().get(i)));
        }

        ModelPage<QuestionCategoryResponse> modelPage = new ModelPage<>(categoryResponses, pagination);

        return ResponseEntity.ok(modelPage);
    }

    @Override
    public ResponseEntity<?> enable(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory category = questionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        category.setUpdatedDate(new Date());
        category.setUpdatedUser(updater);
        category.setEnable(true);
        questionCategoryRepository.save(category);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateEnable(String token, Long id, UpdateEnableRequest enable) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory category = questionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        category.setEnable(enable.getEnable());
        category.setUpdatedUser(updater);
        category.setUpdatedDate(new Date());
        category = questionCategoryRepository.save(category);

        return ResponseEntity.ok(questionCategoryMapper.toResponse(category));
    }

    private long getApprovedQuantity(QuestionCategory questionCategory) {
        return questionCategory.getQuestions()
                .stream()
                .filter(q -> q.getEnable() && q.getStatus().equals(QuestionStatus.APPROVED))
                .count();
    }
}