package com.tma.recruit.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "badge_id")
    private String badgeId;

    @Column(name = "username", unique = true)
    private String username;

    @Email
    @Column(name = "email", unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @OneToMany(mappedBy = "approver")
    private List<QuestionBank> questions;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<Notification> notifications;

    @OneToMany(mappedBy = "receiver", cascade = {CascadeType.ALL})
    private List<NotificationReceiver> notificationReceivers;
}