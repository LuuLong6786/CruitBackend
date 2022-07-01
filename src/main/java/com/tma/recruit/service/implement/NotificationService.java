package com.tma.recruit.service.implement;

import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.mapper.UserMapper;
import com.tma.recruit.model.response.UserNotificationResponse;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.service.interfaces.INotificationService;
import com.tma.recruit.util.RoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService implements INotificationService {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> notifyUserCreationToAdmin(User user) {

        List<User> admins = userRepository.findByRolesNameContaining(RoleConstant.ADMIN);

        UserNotificationResponse response = new UserNotificationResponse();
        response.setContent("User " + user.getUsername() + " has been created");
        response.setUser(userMapper.toResponse(user));
        response.setTime(new Date());

        admins.forEach(admin -> {
            template.convertAndSendToUser(admin.getUsername(), "/queue/notification", response);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }
}