package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.Role;
import com.tma.recruit.model.request.RoleRequest;
import com.tma.recruit.model.response.RoleResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {})
public interface RoleMapper extends EntityMapper<Role, RoleResponse, RoleRequest> {

//    public List<RoleResponse> toResponse(List<Role> entityList) {
//        List<RoleResponse> roleResponses = new ArrayList<>();
//        entityList.forEach(permission -> roleResponses.add(toResponse(permission)));
//        return roleResponses;
//    }
}