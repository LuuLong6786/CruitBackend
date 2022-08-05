package com.tma.recruit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_bank_template")
public class QuestionBankTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_no")
    private Integer questionNo;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionBank question;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "template_id")
    private QuestionTemplate template;
}
