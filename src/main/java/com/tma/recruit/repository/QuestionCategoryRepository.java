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

    boolean existsByNameIgnoreCaseAndActiveTrue(String name);

    Optional<QuestionCategory> findByIdAndActiveTrue(Long id);

    List<QuestionCategory> findByActiveTrue();

    boolean existsByNameIgnoreCase(String name);

    Optional<QuestionCategory> findByNameIgnoreCaseAndActiveTrue(String name);

    Optional<QuestionCategory> findByNameIgnoreCase(String name);

    @Query(value = "SELECT c " +
            "FROM QuestionCategory c " +
            "WHERE " +
            "(name LIKE CONCAT('%',:keyword,'%') OR :keyword is null) " +
            "AND (active = :active OR :active is null)")
    Page<QuestionCategory> filter(String keyword, Boolean active, Pageable paging);
}
