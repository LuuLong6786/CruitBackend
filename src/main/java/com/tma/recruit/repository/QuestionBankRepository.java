package com.tma.recruit.repository;

import com.tma.recruit.model.entity.QuestionBank;
import com.tma.recruit.model.enums.QuestionLevelEnum;
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

    List<QuestionBank> findByApprovedTrueAndActiveTrue();

    @Query(value = "select u.* from question_bank u "
    ,nativeQuery = true)
    Page<QuestionBank> filter(QuestionLevelEnum level, Long categoryId, Long criterionId, Pageable paging);
}