package com.tma.recruit.model.request;

import com.tma.recruit.model.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {

    @Column(name = "name")
    private String name;

    private List<PermissionRequest> permissions;
}
