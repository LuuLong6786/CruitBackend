package com.tma.recruit.repository;

import com.tma.recruit.model.entity.QuestionCriterion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionCriterionRepository extends JpaRepository<QuestionCriterion, Long> {

    boolean existsByNameIgnoreCaseAndEnableTrue(String criterion);

    Optional<QuestionCriterion> findByIdAndEnableTrue(Long id);

    List<QuestionCriterion> findByEnableTrue();

    boolean existsByNameIgnoreCase(String name);

    Optional<QuestionCriterion> findByNameIgnoreCase(String name);

    @Query(value = "select *\n" +
            "from question_criterion\n" +
            "where\n" +
            "(name like CONCAT('%',:keyword,'%') or :keyword is null)\n" +
            "and (enable = :enable or :enable is null)",
            nativeQuery = true)
    Page<QuestionCriterion> filter(String keyword, Boolean enable, Pageable paging);
}
