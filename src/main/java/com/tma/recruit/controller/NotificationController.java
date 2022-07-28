package com.tma.recruit.controller;

import com.tma.recruit.service.interfaces.INotificationService;
import com.tma.recruit.util.Constant;
import com.tma.recruit.util.PaginationConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotification(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return notificationService.getUnreadNotification(token);
    }

    @GetMapping
    public ResponseEntity<?> getAllNotification(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                                @RequestParam(required = false, defaultValue =
                                                        PaginationConstant.PAGE_SIZE_DEFAULT_VALUE) Integer pageSize,
                                                @RequestParam(required = false, defaultValue =
                                                        PaginationConstant.PAGE_DEFAULT_VALUE) Integer page) {
        return notificationService.getAllNotification(token, pageSize, page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token,
                                     @PathVariable Long id) {
        return notificationService.getById(token, id);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadNotificationNumber(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token) {
        return notificationService.getUnreadNotificationNumber(token);
    }

    @PostMapping("/read-all")
    public ResponseEntity<?> readAll(@RequestHeader(Constant.AUTHENTICATION_HEADER) String token){
        return notificationService.readAll(token);
    }
}