package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.UserEntity;
import com.tma.recruit.model.request.UserRequest;
import com.tma.recruit.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = {})
public abstract class UserMapper implements EntityMapper<UserEntity, UserResponse, UserRequest> {

    @Mapping(ignore = true, target = "password")
    public abstract UserEntity toEntity(UserRequest model);

    @Mapping(ignore = true, target = "password")
    @Mapping(ignore = true, target = "email")
    public abstract void partialUpdate(@MappingTarget UserEntity entity, UserRequest model);
}
