package com.tma.recruit.model.entity;

import com.tma.recruit.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionBank questionBank;

    @ManyToOne
    @JoinColumn(name = "question_template_id")
    private QuestionTemplate questionTemplate;

    @OneToMany(mappedBy = "notification", cascade = {CascadeType.ALL})
    private List<NotificationReceiver> notificationReceivers;

    public Notification(User user) {
        this.setUpdatedUser(user);
        this.setAuthor(user);
    }
}
