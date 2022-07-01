package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.entity.User;
import org.springframework.http.ResponseEntity;

public interface INotificationService {

    ResponseEntity<?> notifyUserCreationToAdmin(User user);
}
