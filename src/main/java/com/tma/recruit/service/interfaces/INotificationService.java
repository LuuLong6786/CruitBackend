package com.tma.recruit.service.interfaces;

import com.tma.recruit.model.entity.QuestionBank;
import com.tma.recruit.model.entity.QuestionTemplate;
import com.tma.recruit.model.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface INotificationService {

    ResponseEntity<?> notifyCreationToAdmin(User user);

    ResponseEntity<?> notifyCreationToAdmin(QuestionBank questionBank);

    ResponseEntity<?> notifyUpdateToAdmin(User user);

    ResponseEntity<?> notifyUpdateToAdmin(QuestionBank questionBank);

    ResponseEntity<?> getAllNotification(String token, Boolean read, Integer pageSize, Integer page);

    ResponseEntity<?> getById(String token, Long id);

    ResponseEntity<?> getUnreadNotificationNumber(String token);

    ResponseEntity<?> readAll(String token);

    void notifySharingTemplate(QuestionTemplate questionTemplate);
}
