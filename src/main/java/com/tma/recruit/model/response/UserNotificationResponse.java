package com.tma.recruit.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationResponse {

    private Date time;

    private String content;

    private UserResponse user;
}
