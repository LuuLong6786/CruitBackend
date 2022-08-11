package com.tma.recruit.model.entity;

import com.tma.recruit.model.enums.QuestionTemplateStatus;
import com.tma.recruit.model.enums.QuestionTemplateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_template")
public class QuestionTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "is_public")
    private boolean isPublic = false;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private QuestionCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_template_type")
    private QuestionTemplateType questionTemplateType = QuestionTemplateType.PERSONAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private QuestionTemplateStatus status = QuestionTemplateStatus.PENDING;

    @OneToMany(mappedBy = "template", cascade = {CascadeType.ALL})
    private List<QuestionBankTemplate> questionBankTemplates;

    @OneToMany(mappedBy = "questionTemplate", cascade = {CascadeType.ALL})
    private List<Notification> notifications;

    public QuestionTemplate(String name, String description, Boolean isPublic,
                            QuestionTemplateType questionTemplateType) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.questionTemplateType = questionTemplateType;
    }

    public QuestionTemplate cloneTemplateForSharing() {
        return new QuestionTemplate(this.name, this.description, true, QuestionTemplateType.SHARING);
    }

    public QuestionTemplate cloneTemplateForPull() {
        return new QuestionTemplate(this.name, this.description, false, QuestionTemplateType.PERSONAL);
    }
}