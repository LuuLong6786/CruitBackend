package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.Role;
import com.tma.recruit.model.request.RoleRequest;
import com.tma.recruit.model.response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = {})
public abstract class RoleMapper implements EntityMapper<Role, RoleResponse, RoleRequest> {

    @Mapping(ignore = true, target = "permissions")
    public abstract RoleResponse toResponse(Role entity);

    public List<RoleResponse> toResponse(List<Role> entityList) {
        List<RoleResponse> roleResponses = new ArrayList<>();
        entityList.forEach(permission -> roleResponses.add(toResponse(permission)));
        return roleResponses;
    }
}