package com.tma.recruit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_bank")
public class QuestionBank extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "category_id")
    private QuestionCategory category;

    @Lob
    @Column(name = "content")
    private String content;

    @Lob
    @Column(name = "answer")
    private String answer;

    @Column(name = "approved_date")
    private Date approvedDate;

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private User approver;

    @ManyToMany
    @JoinTable(name = "question_bank_criteria",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "criteria_id"))
    private List<QuestionCriteria> criteria;
}