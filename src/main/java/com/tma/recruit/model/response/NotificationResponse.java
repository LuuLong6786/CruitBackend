package com.tma.recruit.model.response;

import com.tma.recruit.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;

    private List<UserResponse> receivers;

    private Boolean read = false;

    private String content;

    private UserResponse user;

    private NotificationType notificationType;

    private QuestionBankResponse questionBank;
}
