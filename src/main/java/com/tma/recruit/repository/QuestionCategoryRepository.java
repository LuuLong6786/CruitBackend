package com.tma.recruit.repository;

import com.tma.recruit.model.entity.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, Long> {

    boolean existsByNameIgnoreCaseAndActiveTrue(String name);

    Optional<QuestionCategory> findByIdAndActiveTrue(Long id);

    List<QuestionCategory> findByActiveTrue();

    boolean existsByNameIgnoreCase(String name);

    Optional<QuestionCategory> findByNameIgnoreCaseAndActiveTrue(String name);

    Optional<QuestionCategory> findByNameIgnoreCase(String name);
}
