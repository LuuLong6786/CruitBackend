package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.request.UserRequest;
import com.tma.recruit.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {})
public abstract class UserMapper implements EntityMapper<User, UserResponse, UserRequest> {

    @Mapping(ignore = true, target = "password")
    public abstract User toEntity(UserRequest model);

    @Mapping(ignore = true, target = "password")
    @Mapping(ignore = true, target = "email")
    @Mapping(ignore = true, target = "roles")
    public abstract void partialUpdate(@MappingTarget User entity, UserRequest model);
}
