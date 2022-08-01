package com.tma.recruit.repository;

import com.tma.recruit.model.entity.NotificationReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationReceiverRepository extends JpaRepository<NotificationReceiver, Long> {

    Optional<NotificationReceiver> findByReceiverIdAndNotificationId(Long receiveId, Long id);

    List<NotificationReceiver> findByReceiverIdAndReadFalse(Long id);

    List<NotificationReceiver> findByReceiverId(Long id);
}
