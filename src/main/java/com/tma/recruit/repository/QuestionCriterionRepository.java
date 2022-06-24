package com.tma.recruit.repository;

import com.tma.recruit.model.entity.QuestionCriterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionCriterionRepository extends JpaRepository<QuestionCriterion, Long> {

    boolean existsByNameAndActiveTrue(String criterion);

    Optional<QuestionCriterion> findByIdAndActiveTrue(Long id);

    List<QuestionCriterion> findByActiveTrue();
}
