package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.request.UserRequest;
import com.tma.recruit.model.response.UserDetailResponse;
import com.tma.recruit.model.response.UserResponse;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = {})
public interface UserMapper extends EntityMapper<User, UserResponse, UserRequest> {

    UserDetailResponse toDetailResponse(User entity);

    List<UserDetailResponse> toDetailResponse(List<User> entity);

    @Mapping(ignore = true, target = "password")
    User toEntity(UserRequest model);

    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "password")
    @Mapping(ignore = true, target = "email")
    @Mapping(ignore = true, target = "roles")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget User entity, UserRequest model);
}
