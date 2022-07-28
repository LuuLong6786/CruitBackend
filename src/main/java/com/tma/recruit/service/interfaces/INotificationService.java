package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.entity.QuestionBank;
import com.tma.recruit.model.entity.User;
import org.springframework.http.ResponseEntity;

public interface INotificationService {

    ResponseEntity<?> notifyCreationToAdmin(User user);

    ResponseEntity<?> notifyCreationToAdmin(QuestionBank questionBank);

    ResponseEntity<?> notifyUpdateToAdmin(User user);

    ResponseEntity<?> notifyUpdateToAdmin(QuestionBank questionBank);

    ResponseEntity<?> getAllNotification(String token, Integer pageSize, Integer page);

    ResponseEntity<?> getUnreadNotification(String token);

    ResponseEntity<?> getById(String token, Long id);

    ResponseEntity<?> getUnreadNotificationNumber(String token);

    ResponseEntity<?> readAll(String token);
}
