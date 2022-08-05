package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.Notification;
import com.tma.recruit.model.response.NotificationResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = {})
public interface NotificationMapper extends EntityMapper<Notification, NotificationResponse, NotificationResponse> {

    NotificationResponse toResponse(Notification entity);

    List<NotificationResponse> toResponse(List<Notification> entityList);
}