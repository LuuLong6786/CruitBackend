package com.tma.recruit.repository;

import com.tma.recruit.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByNotificationReceiversReceiverIdAndEnableTrueOrderByIdDesc(
            Long id, Pageable paging);

    List<Notification> findByNotificationReceiversReceiverIdAndNotificationReceiversReadFalseAndEnableTrueOrderByIdDesc(
            Long id);

    @Query(value = "select COUNT(notification.id) from notification ,notification_receiver\n" +
            "where \n" +
            "notification.id = notification_receiver.notification_id\n" +
            "and notification_receiver.receiver_id  = :id\n" +
            "and notification_receiver.is_read = false",
            nativeQuery = true)
    Long countUnreadNotificationNumber(Long id);
}