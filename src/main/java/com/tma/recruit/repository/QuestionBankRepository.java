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

    @Query(value = "select question_bank.*\n" +
            "from question_bank,\n" +
            "question_category,\n" +
            "question_criterion,\n" +
            "question_bank_criteria\n" +
            "where\n" +
            "question_bank.category_id = question_category.id\n" +
            "and question_bank_criteria.question_id = question_bank.id\n" +
            "and question_bank_criteria.criteria_id=question_criterion.id\n" +
            "and (question_bank.level = :level or :level is null)\n" +
            "and (question_bank.status = :status or :status is null)\n" +
            "and (question_bank.content like CONCAT('%',:keyword,'%') or :keyword is null)\n" +
            "and (question_criterion.id= :criterionId or :criterionId is null)\n" +
            "and (question_category.id= :categoryId or :categoryId is null)\n" +
            "and question_bank.enable=true\n" +
            "group by question_bank.id",
            nativeQuery = true)
    Page<QuestionBank> filter(String level, Long categoryId, Long criterionId, String keyword, String status,
                              Pageable paging);
}