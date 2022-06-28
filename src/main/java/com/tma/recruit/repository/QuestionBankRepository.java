package com.tma.recruit.repository;

import com.tma.recruit.model.entity.QuestionBank;
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

//    List<QuestionBank> findByApprovedTrueAndActiveTrue();

    @Query(value = "select q.* " +
            "from question_bank q, " +
            "question_category cat, " +
            "question_criterion cri, " +
            "question_bank_criteria bc " +
            "where q.category_id = cat.id " +
            "and bc.question_id = q.id " +
            "and bc.criteria_id=cri.id " +
            "and (q.level = :level or :level is null) " +
            "and (q.status = :status or :status is null) " +
            "and (q.content like CONCAT('%',:keyword,'%') or :keyword is null) " +
            "and (cri.id= :criterionId or :criterionId is null) " +
            "and (cat.id= :categoryId or :categoryId is null) " +
            "group by q.id",
            nativeQuery = true)
    Page<QuestionBank> filter(String level, Long categoryId, Long criterionId, Pageable paging, String keyword,
                              String status);
}