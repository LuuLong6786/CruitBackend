package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.Notification;
import com.tma.recruit.model.entity.NotificationReceiver;
import com.tma.recruit.model.entity.QuestionBank;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.NotificationType;
import com.tma.recruit.model.mapper.NotificationMapper;
import com.tma.recruit.model.mapper.UserMapper;
import com.tma.recruit.model.response.ModelPage;
import com.tma.recruit.model.response.NotificationResponse;
import com.tma.recruit.model.response.Pagination;
import com.tma.recruit.model.response.UnreadNotificationNumberResponse;
import com.tma.recruit.repository.NotificationReceiverRepository;
import com.tma.recruit.repository.NotificationRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.security.jwt.JwtUtils;
import com.tma.recruit.service.interfaces.INotificationService;
import com.tma.recruit.util.Constant;
import com.tma.recruit.util.RoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private NotificationReceiverRepository notificationReceiverRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public ResponseEntity<?> notifyCreationToAdmin(User user) {
        List<User> admins = getAdminList();
        admins = admins.stream().filter(
                admin ->(!admin.getId().equals(user.getAuthor().getId()))).collect(Collectors.toList());

        Notification notification = new Notification(user);
        notification.setContent("User " + user.getUsername() + " has been created");
        notification.setUser(user);
        notification.setNotificationType(NotificationType.USER);
        notification = notificationRepository.save(notification);

        saveNotificationReceivers(admins, notification);

        NotificationResponse response = notificationMapper.toResponse(notification);
        admins.forEach(receiver -> {
            template.convertAndSendToUser(receiver.getUsername(), Constant.QUEUE_NOTIFICATION_URL, response);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> notifyCreationToAdmin(QuestionBank questionBank) {
        User user = questionBank.getAuthor();
        List<User> admins = getAdminList();
        admins = admins.stream().filter(
                admin ->(!admin.getId().equals(user.getId()))).collect(Collectors.toList());

        Notification notification = new Notification(user);
        notification.setContent(user.getUsername() + " just created the question");
        notification.setUser(user);
        notification.setQuestionBank(questionBank);
        notification.setNotificationType(NotificationType.QUESTION);
        notification = notificationRepository.save(notification);

        saveNotificationReceivers(admins, notification);

        NotificationResponse response = notificationMapper.toResponse(notification);
        admins.forEach(receiver -> {
            template.convertAndSendToUser(receiver.getUsername(), Constant.QUEUE_NOTIFICATION_URL, response);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> notifyUpdateToAdmin(User user) {
        List<User> admins = getAdminList();
        admins = admins.stream().filter(
                admin ->(!admin.getId().equals(user.getUpdatedUser().getId()))).collect(Collectors.toList());

        Notification notification = new Notification(user);
        notification.setContent("User " + user.getUsername() + " has been updated");
        notification.setUser(user);
        notification.setNotificationType(NotificationType.USER);
        notification = notificationRepository.save(notification);

        saveNotificationReceivers(admins, notification);

        NotificationResponse response = notificationMapper.toResponse(notification);

        admins.forEach(receiver -> {
            template.convertAndSendToUser(receiver.getUsername(), Constant.QUEUE_NOTIFICATION_URL, response);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> notifyUpdateToAdmin(QuestionBank questionBank) {
        User user = questionBank.getUpdatedUser();
        List<User> admins = getAdminList();
        admins = admins.stream().filter(
                admin ->(!admin.getId().equals(user.getId()))).collect(Collectors.toList());

        Notification notification = new Notification(user);
        notification.setContent(user.getUsername() + " just edited the question");
        notification.setUser(user);
        notification.setQuestionBank(questionBank);
        notification.setNotificationType(NotificationType.QUESTION);
        notification = notificationRepository.save(notification);

        saveNotificationReceivers(admins, notification);

        NotificationResponse response = notificationMapper.toResponse(notification);
        admins.forEach(receiver -> {
            template.convertAndSendToUser(receiver.getUsername(), Constant.QUEUE_NOTIFICATION_URL, response);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAllNotification(String token, Integer pageSize, Integer page) {
        Pageable paging = PageRequest.of(page - 1, pageSize);

        User user = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Page<Notification> notifications = notificationRepository
                .findByNotificationReceiversReceiverIdAndEnableTrueOrderByIdDesc(
                        user.getId(), paging);

        Pagination pagination = new Pagination(pageSize, page, notifications.getTotalPages(),
                notifications.getTotalElements());

        List<NotificationResponse> response = notificationMapper.toResponse(notifications.getContent());
        addReadToResponse(user, notifications.getContent(), response);

        ModelPage<NotificationResponse> modelPage = new ModelPage<>(response, pagination);

        return ResponseEntity.ok(modelPage);
    }

    @Override
    public ResponseEntity<?> getUnreadNotification(String token) {
//        Optional<User> user = userRepository
//                .findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token));
//        if (user.isPresent()) {
//            List<Notification> notifications = notificationRepository
//                    .findByReceiversIdContainingAndReadFalseAndEnableTrue(user.get().getId());
//            return ResponseEntity.ok(notificationMapper.toResponse(notifications));
//        } else {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
//        }
        return null;
    }

    @Override
    public ResponseEntity<?> getById(String token, Long id) {
        User user = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        NotificationReceiver notificationReceiver = notificationReceiverRepository
                .findByReceiverIdAndNotificationId(user.getId(), id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        notificationReceiver.setRead(true);
        notificationReceiverRepository.save(notificationReceiver);

        return ResponseEntity.ok(notificationMapper.toResponse(notification));
    }

    @Override
    public ResponseEntity<?> getUnreadNotificationNumber(String token) {
        Long unreadNotificationNumber = notificationRepository.countUnreadNotificationNumber(
                jwtUtils.getIdByJwtToken(token));

        return ResponseEntity.ok(new UnreadNotificationNumberResponse(unreadNotificationNumber));
    }

    @Override
    public ResponseEntity<?> readAll(String token) {
        User user = userRepository.findByUsernameIgnoreCaseAndEnableTrue(jwtUtils.getUsernameFromJwtToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        List<NotificationReceiver> notificationReceivers = notificationReceiverRepository.findByReceiverIdAndReadFalse(user.getId());
        notificationReceivers.forEach(notificationReceiver -> notificationReceiver.setRead(true));
        notificationReceiverRepository.saveAll(notificationReceivers);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private List<User> getAdminList() {
        return userRepository.findByRolesNameContainingIgnoreCaseAndEnableTrue(RoleConstant.ADMIN);
    }

    private void saveNotificationReceivers(List<User> receiver, Notification notification) {
        List<NotificationReceiver> notificationReceivers = new ArrayList<>();
        for (User admin : receiver) {
            NotificationReceiver notificationReceiver = new NotificationReceiver();
            notificationReceiver.setNotification(notification);
            notificationReceiver.setReceiver(admin);
            notificationReceivers.add(notificationReceiver);
        }
        notificationReceiverRepository.saveAll(notificationReceivers);
    }

    private void addReadToResponse(User user, List<Notification> notifications,
                                   List<NotificationResponse> notificationResponses) {
        for (int i = 0; i < notifications.size(); i++) {
            Optional<NotificationReceiver> notificationReceiver = notifications.get(i).getNotificationReceivers()
                    .stream()
                    .filter(nr -> (nr.getReceiver().getUsername().equals(user.getUsername())
                            && nr.getNotification().getId().equals(nr.getNotification().getId()))).findFirst();
            if (notificationReceiver.isPresent()) {
                notificationResponses.get(i).setRead(notificationReceiver.get().getRead());
            }
        }
    }
}