package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.Permission;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.request.PermissionRequest;
import com.tma.recruit.model.response.PermissionResponse;
import com.tma.recruit.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = {})
public abstract class PermissionMapper implements EntityMapper<Permission, PermissionResponse, PermissionRequest> {

    @Autowired
    private UserMapper userMapper;

    @Mapping(source = "author", target = "author", qualifiedByName = "toUserResponse")
    @Mapping(source = "updatedUser", target = "updatedUser", qualifiedByName = "toUserResponse")
    public abstract PermissionResponse toResponse(Permission entity);

    public List<PermissionResponse> toResponse(List<Permission> entityList) {
        List<PermissionResponse> permissionResponses = new ArrayList<>();
        entityList.forEach(permission -> permissionResponses.add(toResponse(permission)));
        return permissionResponses;
    }

    @Named("toUserResponse")
    public UserResponse toUserResponse(User user) {
        return userMapper.toResponse(user);
    }
}
