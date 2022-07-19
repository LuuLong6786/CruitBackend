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

    Optional<QuestionBank> findByIdAndEnableTrue(Long id);

    List<QuestionBank> findByEnableTrue();

    @Query(value = "select question_bank.* " +
            "from question_bank, " +
            "question_category, " +
            "question_criterion, " +
            "question_bank_criteria  " +
            "where " +
            "question_bank.category_id = question_category.id " +
            "and question_bank_criteria.question_id = question_bank.id " +
            "and question_bank_criteria.criteria_id=question_criterion.id " +
            "and (question_bank.level = :level or :level is null) " +
            "and (question_bank.status = :status or :status is null) " +
            "and (question_bank.content like CONCAT('%',:keyword,'%') or :keyword is null) " +
            "and (question_criterion.id= :criterionId or :criterionId is null) " +
            "and (question_category.id= :categoryId or :categoryId is null) " +
            "and question_bank.active=true " +
            "group by question_bank.id ",
            nativeQuery = true)
    Page<QuestionBank> filter(String level, Long categoryId, Long criterionId, Pageable paging, String keyword,
                              String status);
}