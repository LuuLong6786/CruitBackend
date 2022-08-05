package com.tma.recruit.repository;

import com.tma.recruit.model.entity.QuestionTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionTemplateRepository extends JpaRepository<QuestionTemplate, Long> {

    Optional<QuestionTemplate> findByIdAndIsPublicTrue(Long id);

    @Query(value = "SELECT * " +
            "FROM question_template " +
            "WHERE " +
            "(status = :status OR :status is null) " +
            "AND (question_template_type = :templateType OR :templateType is null) " +
            "AND (category_id = :categoryId OR :categoryId is null) " +
            "AND (name LIKE CONCAT('%',:keyword,'%') OR description LIKE CONCAT('%',:keyword,'%') OR :keyword is null)",
            nativeQuery = true)
    Page<QuestionTemplate> filterByAdmin(String status, Long categoryId, String keyword, String templateType,
                                         Pageable paging);

    @Query(value = "select * " +
            "from question_template " +
            "where " +
            "(is_public = :isPublic or :isPublic is null) " +
            "and (category_id = :categoryId or :categoryId is null) " +
            "and (question_template_type = :templateType or :templateType is null) " +
            "AND (name LIKE CONCAT('%',:keyword,'%') OR description LIKE CONCAT('%',:keyword,'%') " +
            "OR :keyword is null) " +
            "and author_id = :userId", nativeQuery = true)
    Page<QuestionTemplate> filterByUser(Boolean isPublic, Long categoryId, String templateType, String keyword,
                                        Long userId, Pageable paging);
}