package com.tma.recruit.repository;

import com.tma.recruit.model.entity.QuestionCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionCriteriaRepository extends JpaRepository<QuestionCriteria, Long> {
}
