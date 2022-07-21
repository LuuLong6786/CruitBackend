package com.tma.recruit.util;

public class PreAuthorizerConstant {

    public static final String ADMIN_ROLE = "hasRole('" + RoleConstant.ADMIN + "')";

    public static final String USER_ROLE = "hasRole('" + RoleConstant.USER + "')";

    public static final String GUEST_ROLE = "hasRole('" + RoleConstant.GUEST + "')";

    public static final String CREATE_AUTHORITY = "hasAuthority('" + PermissionConstant.CREATE + "')";

    public static final String UPDATE_AUTHORITY = "hasAuthority('" + PermissionConstant.UPDATE + "')";

    public static final String DELETE_AUTHORITY = "hasAuthority('" + PermissionConstant.DELETE + "')";

    public static final String VIEW_AUTHORITY = "hasAuthority('" + PermissionConstant.VIEW + "')";
}