package com.tma.recruit.repository;

import com.tma.recruit.model.entity.QuestionBank;
import com.tma.recruit.model.enums.QuestionLevel;
import com.tma.recruit.model.enums.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {

    Optional<QuestionBank> findByIdAndActiveTrue(Long id);

    List<QuestionBank> findByActiveTrue();

    @Query(value = "SELECT q " +
            "FROM QuestionBank q " +
            "LEFT OUTER JOIN q.criteria cri " +
            "WHERE " +
            "(q.level = :level OR :level is null) " +
            "AND (q.status = :status OR :status is null) " +
            "AND (q.content  LIKE CONCAT('%',:keyword,'%') OR :keyword is null) " +
            "AND (cri.id = :criterionId OR :criterionId is null) " +
            "AND (q.category.id = :categoryId OR :categoryId is null) " +
            "AND q.active = true " +
            "GROUP BY q.id")
    Page<QuestionBank> filter(QuestionLevel level, Long categoryId, Long criterionId, String keyword, QuestionStatus status,
                              Pageable paging);
}