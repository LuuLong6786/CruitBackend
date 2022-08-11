package com.tma.recruit.repository;

import com.tma.recruit.model.entity.QuestionTemplate;
import com.tma.recruit.model.enums.QuestionTemplateStatus;
import com.tma.recruit.model.enums.QuestionTemplateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionTemplateRepository extends JpaRepository<QuestionTemplate, Long> {

    Optional<QuestionTemplate> findByIdAndIsPublicTrue(Long id);

    @Query(value = "SELECT t " +
            "FROM QuestionTemplate t " +
            "WHERE " +
            "(status = :status OR :status is null) " +
            "AND (questionTemplateType = :templateType or :templateType is null) " +
            "AND (category.id = :categoryId OR :categoryId is null) " +
            "AND (name LIKE CONCAT('%',:keyword,'%') OR description LIKE CONCAT('%',:keyword,'%') OR :keyword is null)")
    Page<QuestionTemplate> filterByAdmin(QuestionTemplateStatus status, Long categoryId, String keyword,
                                         QuestionTemplateType templateType, Pageable paging);

    @Query(value = "SELECT t " +
            "FROM QuestionTemplate t " +
            "WHERE " +
            "(isPublic =:isPublic OR :isPublic is null) " +
            "AND (category.id = :categoryId OR :categoryId is null) " +
            "AND (questionTemplateType = :templateType OR :templateType is null) " +
            "AND (name LIKE CONCAT('%',:keyword,'%') OR description LIKE CONCAT('%',:keyword,'%') " +
            "OR :keyword is null) " +
            "AND author.id = :userId")
    Page<QuestionTemplate> filterByUser(Boolean isPublic, Long categoryId, QuestionTemplateType templateType,
                                        String keyword, Long userId, Pageable paging);

    @Query(value = "SELECT t " +
            "FROM QuestionTemplate t " +
            "WHERE " +
            "(category.id = :categoryId OR :categoryId is null) " +
            "AND (name LIKE CONCAT('%',:keyword,'%') OR description LIKE CONCAT('%',:keyword,'%') " +
            "OR :keyword is null)")
    Page<QuestionTemplate> explore(Long categoryId, String keyword, Pageable paging);
}