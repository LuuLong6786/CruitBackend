package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.*;
import com.tma.recruit.model.enums.QuestionTemplateStatus;
import com.tma.recruit.model.enums.QuestionTemplateType;
import com.tma.recruit.model.enums.SortType;
import com.tma.recruit.model.mapper.QuestionTemplateMapper;
import com.tma.recruit.model.request.QuestionTemplateRequest;
import com.tma.recruit.model.response.ModelPage;
import com.tma.recruit.model.response.Pagination;
import com.tma.recruit.model.response.QuestionTemplateResponse;
import com.tma.recruit.repository.*;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.IQuestionTemplateService;
import com.tma.recruit.service.interfaces.IUserService;
import com.tma.recruit.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionTemplateService implements IQuestionTemplateService {

    @Autowired
    private QuestionTemplateRepository questionTemplateRepository;

    @Autowired
    private QuestionTemplateMapper questionTemplateMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Autowired
    private QuestionBankTemplateRepository questionBankTemplateRepository;

    @Autowired
    private QuestionCategoryRepository questionCategoryRepository;

    @Autowired
    private IUserService userService;

    @Override
    public ResponseEntity<?> create(String token, QuestionTemplateRequest request) {
        User author = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        request.setQuestionBankTemplates(request.getQuestionBankTemplates().stream().distinct().collect(Collectors.toList()));

        QuestionTemplate template = questionTemplateMapper.toEntity(request);
        template.setAuthor(author);
        template.setUpdatedUser(author);
        template = questionTemplateRepository.save(template);

        saveQuestionBankTemplates(request, template);

        template = questionTemplateRepository.findById(template.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.status(HttpStatus.CREATED).body(questionTemplateMapper.toResponse(template));
    }

    @Override
    public ResponseEntity<?> update(String token, Long id, QuestionTemplateRequest request) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionTemplate template = questionTemplateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (modifiable(updater, template)) {
            questionTemplateMapper.partialUpdate(template, request);
            if (request.getCategory() != null
                    && request.getCategory().getId() != null
                    && request.getCategory().getId() > 0) {
                QuestionCategory category = questionCategoryRepository
                        .findByIdAndActiveTrue(request.getCategory().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                template.setCategory(category);
            }
            template.setUpdatedDate(new Date());
            template.setUpdatedUser(updater);
            template = questionTemplateRepository.save(template);

            if (!request.getQuestionBankTemplates().isEmpty()) {
                List<QuestionBankTemplate> questionBankTemplates = template.getQuestionBankTemplates();
                template.setQuestionBankTemplates(null);
                questionBankTemplateRepository.deleteAll(questionBankTemplates);
                saveQuestionBankTemplates(request, template);
            }
            QuestionTemplate newTemplate = questionTemplateRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            return ResponseEntity.ok(questionTemplateMapper.toResponse(newTemplate));
        } else {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @Override
    public ResponseEntity<?> delete(String token, Long id) {
        User user = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        QuestionTemplate template = questionTemplateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (userService.isAdmin(user) || (template.getAuthor().getId().equals(user.getId())
                && template.getQuestionTemplateType().equals(QuestionTemplateType.PERSONAL))) {
            questionTemplateRepository.delete(template);

            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<?> getAll() {
        return null;
    }

    @Override
    public ResponseEntity<?> filter(String token, String keyword, Boolean isPublic, QuestionTemplateStatus status,
                                    Long categoryId, QuestionTemplateType templateType, SortType sortType,
                                    String sortBy, Integer page, Integer pageSize) {
        if (jwtUtils.isAdmin(token)) {
            return filterByAdmin(keyword, status, categoryId, templateType, sortType, sortBy, page, pageSize);
        } else {
            return filterByUser(token, keyword, isPublic, categoryId, templateType, sortType, sortBy, page, pageSize);
        }
    }

    @Override
    public ResponseEntity<?> filterByAdmin(String keyword, QuestionTemplateStatus status, Long categoryId,
                                           QuestionTemplateType templateType, SortType sortType, String sortBy,
                                           Integer page, Integer pageSize) {
        PaginationUtil paginationUtil = PaginationUtil.builder()
                .page(page)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortType(sortType)
                .build();
        Pageable paging = paginationUtil.getPageable();
        Page<QuestionTemplate> templates = questionTemplateRepository.filterByAdmin(
                status != null ? status.toString() : null, categoryId, keyword,
                templateType != null ? templateType.toString() : null, paging);
        Pagination pagination = paginationUtil.getPagination(templates);

        List<QuestionTemplateResponse> responses = questionTemplateMapper.toResponse(templates.getContent());

        ModelPage<QuestionTemplateResponse> modelPage = new ModelPage<>(responses, pagination);

        return ResponseEntity.ok(modelPage);
    }

    @Override
    public ResponseEntity<?> filterByUser(String token, String keyword, Boolean isPublic, Long categoryId,
                                          QuestionTemplateType templateType, SortType sortType, String sortBy,
                                          Integer page, Integer pageSize) {
        User user = userRepository.findById(jwtUtils.getUserIdFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        PaginationUtil paginationUtil = PaginationUtil.builder()
                .page(page)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortType(sortType)
                .build();
        Pageable paging = paginationUtil.getPageable();
        Page<QuestionTemplate> templates = questionTemplateRepository.filterByUser(isPublic, categoryId,
                templateType != null ? templateType.toString() : null, keyword, user.getId(), paging);
        Pagination pagination = paginationUtil.getPagination(templates);

        List<QuestionTemplateResponse> responses = questionTemplateMapper.toResponse(templates.getContent());

        ModelPage<QuestionTemplateResponse> modelPage = new ModelPage<>(responses, pagination);

        return ResponseEntity.ok(modelPage);
    }

    @Override
    public ResponseEntity<?> getById(String token, Long id) {
        QuestionTemplate questionTemplate = questionTemplateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (questionTemplate.getAuthor().getId().equals(jwtUtils.getUserIdFromJwtToken(token))
                || questionTemplate.isPublic()) {
            return ResponseEntity.ok(questionTemplateMapper.toResponse(questionTemplate));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<?> approve(String token, Long id) {
        User user = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionTemplate template = questionTemplateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        template.setUpdatedUser(user);
        template.setUpdatedDate(new Date());
        template.setStatus(QuestionTemplateStatus.APPROVED);
        questionTemplateRepository.save(template);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> reject(String token, Long id) {
        User user = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionTemplate template = questionTemplateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        template.setUpdatedUser(user);
        template.setUpdatedDate(new Date());
        template.setStatus(QuestionTemplateStatus.REJECTED);
        questionTemplateRepository.save(template);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> share(String token, Long id) {
        User user = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionTemplate template = questionTemplateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionTemplate sharingTemplate = template.cloneTemplateForSharing();
        sharingTemplate.setAuthor(user);
        sharingTemplate.setUpdatedUser(user);
        sharingTemplate = questionTemplateRepository.save(sharingTemplate);

        saveQuestionBankTemplates(template, sharingTemplate);

        return ResponseEntity.ok(questionTemplateMapper.toResponse(sharingTemplate));
    }

    @Override
    public ResponseEntity<?> explore(String token, String keyword, SortType sortType, String sortBy, Integer page,
                                     Integer pageSize) {
        return null;
    }

    @Override
    public ResponseEntity<?> pullTemplate(String token, Long id) {
        User user = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionTemplate template = questionTemplateRepository.findByIdAndIsPublicTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionTemplate newTemplate = template.cloneTemplateForPull();
        newTemplate.setAuthor(user);
        newTemplate.setUpdatedUser(user);
        newTemplate = questionTemplateRepository.save(newTemplate);

        saveQuestionBankTemplates(template, newTemplate);

        return ResponseEntity.ok(questionTemplateMapper.toResponse(newTemplate));
    }

    @Override
    public ResponseEntity<?> updateStatus(String token, Long id, QuestionTemplateRequest request) {
        User updater = userRepository.findByUsernameIgnoreCaseAndActiveTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        QuestionTemplate template = questionTemplateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        template.setStatus(request.getStatus());
        template.setUpdatedUser(updater);
        template.setUpdatedDate(new Date());
        template = questionTemplateRepository.save(template);

        return ResponseEntity.ok(questionTemplateMapper.toResponse(template));
    }

    private void saveQuestionBankTemplates(QuestionTemplate sourceTemplate, QuestionTemplate targetTemplate) {
        List<QuestionBankTemplate> questionBankTemplates = new ArrayList<>();
        for (QuestionBankTemplate questionBankTemplate : sourceTemplate.getQuestionBankTemplates()) {
            QuestionBankTemplate newQuestionBankTemplate = new QuestionBankTemplate();
            newQuestionBankTemplate.setTemplate(targetTemplate);
            newQuestionBankTemplate.setQuestion(questionBankTemplate.getQuestion());
            newQuestionBankTemplate.setQuestionNo(questionBankTemplate.getQuestionNo());
            questionBankTemplates.add(newQuestionBankTemplate);
        }
        questionBankTemplateRepository.saveAll(questionBankTemplates);
    }

    private void saveQuestionBankTemplates(QuestionTemplateRequest request, QuestionTemplate template) {
        List<QuestionBankTemplate> questionBankTemplates = new ArrayList<>();
        for (int i = 0; i < request.getQuestionBankTemplates().size(); i++) {
            QuestionBank question = questionBankRepository
                    .findByIdAndActiveTrue(request.getQuestionBankTemplates().get(i).getQuestionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            QuestionBankTemplate questionBankTemplate = new QuestionBankTemplate();
            questionBankTemplate.setQuestion(question);
            questionBankTemplate.setTemplate(template);
            questionBankTemplate.setQuestionNo(i + 1);
            questionBankTemplates.add(questionBankTemplate);
        }
        questionBankTemplateRepository.saveAll(questionBankTemplates);
    }

    private boolean modifiable(User updater, QuestionTemplate template) {
        return ((userService.isAdmin(updater)
                && template.getQuestionTemplateType().equals(QuestionTemplateType.SHARING))
                || (template.getAuthor().getId().equals(updater.getId())
                && template.getQuestionTemplateType().equals(QuestionTemplateType.PERSONAL)));
    }
}