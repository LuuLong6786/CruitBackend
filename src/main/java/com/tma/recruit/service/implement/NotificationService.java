package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.Notification;
import com.tma.recruit.model.entity.QuestionBank;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.NotificationType;
import com.tma.recruit.model.mapper.NotificationMapper;
import com.tma.recruit.model.mapper.UserMapper;
import com.tma.recruit.model.response.NotificationResponse;
import com.tma.recruit.model.response.UserNotificationResponse;
import com.tma.recruit.repository.NotificationRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.INotificationService;
import com.tma.recruit.util.Constant;
import com.tma.recruit.util.RoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationService implements INotificationService {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public ResponseEntity<?> test(User user) {

        List<User> admins = userRepository.findByRolesNameContainingIgnoreCaseAndEnableTrue(RoleConstant.ADMIN);

        UserNotificationResponse response = new UserNotificationResponse();
        response.setContent("User " + user.getUsername() + " has been created");
        response.setUser(userMapper.toResponse(user));
        response.setTime(new Date());

        admins.forEach(admin -> {
            template.convertAndSendToUser(admin.getUsername(), Constant.QUEUE_NOTIFICATION_URL, response);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> notifyCreationToAdmin(User user) {
        List<User> admins = userRepository.findByRolesNameContainingIgnoreCaseAndEnableTrue(RoleConstant.ADMIN);

        Notification notification = new Notification();
        notification.setContent("User " + user.getName() + " has been created");
        notification.setUpdatedUser(user);
        notification.setUpdatedDate(new Date());
        notification.setCreatedDate(new Date());
        notification.setAuthor(user);
        notification.setUser(user);
        notification.setReceivers(admins);
        notification.setNotificationType(NotificationType.USER_CREATION);
        notification = notificationRepository.save(notification);

        NotificationResponse response = notificationMapper.toResponse(notification);
        admins.forEach(receiver -> {
            template.convertAndSendToUser(receiver.getUsername(), Constant.QUEUE_NOTIFICATION_URL, response);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> notifyCreationToAdmin(QuestionBank questionBank) {
        User user = questionBank.getUpdatedUser();
        List<User> admins = userRepository.findByRolesNameContainingIgnoreCaseAndEnableTrue(RoleConstant.ADMIN);

        Notification notification = new Notification();
        notification.setContent("User " + user.getName() + " just created the question");
        notification.setUpdatedUser(user);
        notification.setUpdatedDate(new Date());
        notification.setCreatedDate(new Date());
        notification.setAuthor(user);
        notification.setUser(user);
        notification.setReceivers(admins);
        notification.setQuestionBank(questionBank);
        notification.setNotificationType(NotificationType.QUESTION_CREATION);
        notification = notificationRepository.save(notification);

        NotificationResponse response = notificationMapper.toResponse(notification);
        admins.forEach(receiver -> {
            template.convertAndSendToUser(receiver.getUsername(), Constant.QUEUE_NOTIFICATION_URL, response);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> notifyUpdateToAdmin(User user) {
        List<User> admins = userRepository.findByRolesNameContainingIgnoreCaseAndEnableTrue(RoleConstant.ADMIN);

        Notification notification = new Notification();
        notification.setContent("User " + user.getName() + " has been updated");
        notification.setUpdatedUser(user);
        notification.setUpdatedDate(new Date());
        notification.setCreatedDate(new Date());
        notification.setAuthor(user);
        notification.setUser(user);
        notification.setReceivers(admins);
        notification.setNotificationType(NotificationType.USER_UPDATE);
        notification = notificationRepository.save(notification);

        NotificationResponse response = notificationMapper.toResponse(notification);
        admins.forEach(receiver -> {
            template.convertAndSendToUser(receiver.getUsername(), Constant.QUEUE_NOTIFICATION_URL, response);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> notifyUpdateToAdmin(QuestionBank questionBank) {
        User user = questionBank.getUpdatedUser();
        List<User> admins = userRepository.findByRolesNameContainingIgnoreCaseAndEnableTrue(RoleConstant.ADMIN);

        Notification notification = new Notification();
        notification.setContent("User " + user.getName() + " just edited the question");
        notification.setUpdatedUser(user);
        notification.setUpdatedDate(new Date());
        notification.setCreatedDate(new Date());
        notification.setAuthor(user);
        notification.setUser(user);
        notification.setReceivers(admins);
        notification.setQuestionBank(questionBank);
        notification.setNotificationType(NotificationType.QUESTION_UPDATE);
        notification = notificationRepository.save(notification);

        NotificationResponse response = notificationMapper.toResponse(notification);
        admins.forEach(receiver -> {
            template.convertAndSendToUser(receiver.getUsername(), Constant.QUEUE_NOTIFICATION_URL, response);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAllNotification(String token) {
        Optional<User> user = userRepository
                .findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token));
        if (user.isPresent()) {
            List<Notification> notifications = notificationRepository
                    .findByReceiversIdContainingAndEnableTrue(user.get().getId());
            return ResponseEntity.ok(notificationMapper.toResponse(notifications));
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<?> getUnreadNotification(String token) {
        Optional<User> user = userRepository
                .findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token));
        if (user.isPresent()) {
            List<Notification> notifications = notificationRepository
                    .findByReceiversIdContainingAndReadFalseAndEnableTrue(user.get().getId());
            return ResponseEntity.ok(notificationMapper.toResponse(notifications));
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}