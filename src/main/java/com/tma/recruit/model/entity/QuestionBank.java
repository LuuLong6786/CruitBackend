package com.tma.recruit.model.entity;

import com.tma.recruit.model.enums.QuestionLevel;
import com.tma.recruit.model.enums.QuestionStatus;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private QuestionLevel level;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private QuestionStatus status = QuestionStatus.PENDING;

    @ManyToMany
    @JoinTable(name = "question_bank_criterion",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "criterion_id"))
    private List<QuestionCriterion> criteria;
}