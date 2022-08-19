package com.tma.recruit.repository;

import com.tma.recruit.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByNotificationReceiversReceiverIdAndActiveTrueOrderByIdDesc(Long id, Pageable paging);

    Page<Notification> findByNotificationReceiversReceiverIdAndNotificationReceiversReadTrueAndActiveTrueOrderByIdDesc(
            Long id, Pageable paging);

    Page<Notification> findByNotificationReceiversReceiverIdAndNotificationReceiversReadFalseAndActiveTrueOrderByIdDesc(
            Long id, Pageable paging);

    List<Notification> findByUserIdOrAuthorId(Long id1, Long id2);
}