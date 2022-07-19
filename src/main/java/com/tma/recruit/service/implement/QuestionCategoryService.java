package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.QuestionCategory;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.QuestionStatus;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.mapper.QuestionCategoryMapper;
import com.tma.recruit.model.request.QuestionCategoryRequest;
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

        QuestionCategory questionCategory;
        if (questionCategoryRepository.existsByNameIgnoreCaseAndEnableTrue(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else if (questionCategoryRepository.existsByNameIgnoreCase(request.getName())) {
            questionCategory = questionCategoryRepository.findByNameIgnoreCase(request.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            questionCategory.setEnable(true);
            questionCategory.setUpdatedDate(new Date());
        } else {
            questionCategory = questionCategoryMapper.toEntity(request);
            questionCategory.setAuthor(author);
        }
        questionCategory.setUpdatedUser(author);
        questionCategory = questionCategoryRepository.save(questionCategory);

        return ResponseEntity.status(HttpStatus.CREATED).body(questionCategoryMapper.toResponse(questionCategory));
    }

    @Override
    public ResponseEntity<?> update(String token, QuestionCategoryRequest request, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory questionCategory = questionCategoryRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.getName() != null && !questionCategory.getName().equals(request.getName())) {
            if (questionCategoryRepository.existsByNameIgnoreCaseAndEnableTrue(request.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        }

        questionCategoryMapper.partialUpdate(questionCategory, request);
        questionCategory.setUpdatedUser(updater);
        questionCategory.setUpdatedDate(new Date());
        questionCategory = questionCategoryRepository.save(questionCategory);

        return ResponseEntity.ok(questionCategoryMapper.toResponse(questionCategory));
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory questionCategory = questionCategoryRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        questionCategory.setUpdatedDate(new Date());
        questionCategory.setUpdatedUser(updater);
        questionCategory.setEnable(false);
        questionCategoryRepository.save(questionCategory);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(questionCategoryMapper.toResponse(questionCategoryRepository.findByEnableTrue()));
    }

    @Override
    public ResponseEntity<?> getById(Long id) {
        QuestionCategory questionCategory = questionCategoryRepository.findByIdAndEnableTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        long approvedQuantity = questionCategory.getQuestions()
                .stream()
                .filter(q -> q.getEnable() && q.getStatus().equals(QuestionStatus.APPROVED))
                .count();

        QuestionCategoryResponse response = questionCategoryMapper.toResponse(questionCategory);
        response.setApprovedQuantity(approvedQuantity);
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

        ModelPage<QuestionCategoryResponse> modelPage = new ModelPage<>(
                questionCategoryMapper.toResponse(categories.getContent()), pagination);

        return ResponseEntity.ok(modelPage);
    }

    @Override
    public ResponseEntity<?> enable(String token, Long id) {
        User updater = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionCategory questionCategory = questionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        questionCategory.setUpdatedDate(new Date());
        questionCategory.setUpdatedUser(updater);
        questionCategory.setEnable(true);
        questionCategoryRepository.save(questionCategory);

        return ResponseEntity.ok(HttpStatus.OK);
    }
}