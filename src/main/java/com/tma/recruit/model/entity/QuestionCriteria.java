package com.tma.recruit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_criteria")
public class QuestionCriteria extends BaseEntity {

    @Column(name = "criterion")
    private String criterion;

    @ManyToMany(mappedBy = "criteria")
    private List<QuestionBank> questionBanks;
}
