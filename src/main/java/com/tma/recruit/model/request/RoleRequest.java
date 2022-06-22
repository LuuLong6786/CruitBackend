package com.tma.recruit.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {

    private Long id;

    @Column(name = "name")
    private String name;

    private List<PermissionRequest> permissions;
}
