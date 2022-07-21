package com.tma.recruit.repository;

import com.tma.recruit.model.entity.QuestionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, Long> {

    boolean existsByNameIgnoreCaseAndEnableTrue(String name);

    Optional<QuestionCategory> findByIdAndEnableTrue(Long id);

    List<QuestionCategory> findByEnableTrue();

    boolean existsByNameIgnoreCase(String name);

    Optional<QuestionCategory> findByNameIgnoreCaseAndEnableTrue(String name);

    Optional<QuestionCategory> findByNameIgnoreCase(String name);

    @Query(value = "select * " +
            "from question_category " +
            "where " +
            "(name like CONCAT('%',:keyword,'%') or :keyword is null) " +
            "and (enable = :enable or :enable is null) ",
            nativeQuery = true)
    Page<QuestionCategory> filter(String keyword, Boolean enable, Pageable paging);
}
