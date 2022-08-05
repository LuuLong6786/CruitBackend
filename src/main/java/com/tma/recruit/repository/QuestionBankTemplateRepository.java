package com.tma.recruit.repository;

import com.tma.recruit.model.entity.QuestionBankTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionBankTemplateRepository extends JpaRepository<QuestionBankTemplate, Long> {

    List<QuestionBankTemplate> findByTemplateId(Long id);
}
