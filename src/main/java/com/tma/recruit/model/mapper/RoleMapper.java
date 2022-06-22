package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.Role;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.request.RoleRequest;
import com.tma.recruit.model.response.RoleResponse;
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
public abstract class RoleMapper implements EntityMapper<Role, RoleResponse, RoleRequest> {

    @Autowired
    private UserMapper userMapper;

    @Mapping(source = "author", target = "author", qualifiedByName = "toUserResponse")
    @Mapping(source = "updatedUser", target = "updatedUser", qualifiedByName = "toUserResponse")
    public abstract RoleResponse toResponse(Role entity);

    public List<RoleResponse> toResponse(List<Role> entityList) {
        List<RoleResponse> roleResponses = new ArrayList<>();
        entityList.forEach(permission -> roleResponses.add(toResponse(permission)));
        return roleResponses;
    }

    @Named("toUserResponse")
    public UserResponse toUserResponse(User user) {
        return userMapper.toResponse(user);
    }
}
