package com.tma.recruit.repository;

import com.tma.recruit.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiversIdContainingAndActiveTrue(Long id);

    List<Notification> findByReceiversIdContainingAndReadFalseAndActiveTrue(Long id);
}