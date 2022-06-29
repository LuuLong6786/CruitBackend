package com.tma.recruit.model.mapper;

import com.tma.recruit.model.entity.Permission;
import com.tma.recruit.model.request.PermissionRequest;
import com.tma.recruit.model.response.PermissionResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {})
public interface PermissionMapper extends EntityMapper<Permission, PermissionResponse, PermissionRequest> {
}
