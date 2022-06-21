package com.tma.recruit.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tma.recruit.model.entity.Permission;
import com.tma.recruit.model.entity.Role;
import com.tma.recruit.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Long id;

    private final String email;

    @JsonIgnore
    private final String password;

    private final Collection<? extends GrantedAuthority> authorities;

    private List<Role> roles;

    public UserDetailsImpl(Long id, String email, String password, List<Role> roles,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
            authorities.addAll(role.getPermissions().stream()
                    .map(Permission::getPermissionKey)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        });


//        user.getRoles().forEach(role -> authorities.addAll(role.getPermissions().stream()
//                .map(Permission::getPermissionKey)
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList())));

//        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));


//        List<GrantedAuthority> authorities = user.getRoles()
//                .stream()
//                .map(Role::getName)
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRoles(),
                authorities);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.authorities;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getEmail() {
        return email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}