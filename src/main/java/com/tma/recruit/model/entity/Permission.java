package com.tma.recruit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permission")
public class Permission extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "permission_key")
    private String permissionKey;

    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;
}