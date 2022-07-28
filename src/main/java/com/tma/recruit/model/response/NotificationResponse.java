package com.tma.recruit.model.response;

import com.tma.recruit.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;

    private Boolean read;

    private String content;

    private UserResponse user;

    private NotificationType notificationType;

    private QuestionBankResponse questionBank;

    private Date createdDate;

    private Date updatedDate;
}
