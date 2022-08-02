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

    @Query(value = "SELECT question_bank.*\n" +
            "FROM \n" +
            "question_category,\n" +
            "question_bank\n" +
            "LEFT JOIN question_bank_criterion\n" +
            "ON question_bank_criterion.question_id = question_bank.id\n" +
            "LEFT JOIN question_criterion\n" +
            "ON question_bank_criterion.criterion_id = question_criterion.id\n" +
            "WHERE\n" +
            "question_category.id = question_bank.category_id " +
            "AND (question_bank.level = :level or :level is null)\n" +
            "AND (question_bank.status = :status or :status is null)\n" +
            "AND (question_bank.content like CONCAT('%',:keyword,'%') or :keyword is null)\n" +
            "AND (question_criterion.id = :criterionId or :criterionId is null)\n" +
            "AND (question_category.id = :categoryId or :categoryId is null)\n" +
            "AND question_bank.active = true\n" +
            "GROUP BY question_bank.id",
            nativeQuery = true)
    Page<QuestionBank> filter(String level, Long categoryId, Long criterionId, String keyword, String status,
                              Pageable paging);
}