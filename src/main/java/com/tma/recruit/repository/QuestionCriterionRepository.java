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

    boolean existsByNameIgnoreCaseAndActiveTrue(String criterion);

    Optional<QuestionCriterion> findByIdAndActiveTrue(Long id);

    List<QuestionCriterion> findByActiveTrue();

    boolean existsByNameIgnoreCase(String name);

    Optional<QuestionCriterion> findByNameIgnoreCase(String name);

    @Query(value = "SELECT c " +
            "FROM QuestionCriterion c " +
            "WHERE " +
            "(name LIKE CONCAT('%',:keyword,'%')  OR :keyword is null) " +
            "AND (active = :active OR :active is null)")
    Page<QuestionCriterion> filter(String keyword, Boolean active, Pageable paging);
}