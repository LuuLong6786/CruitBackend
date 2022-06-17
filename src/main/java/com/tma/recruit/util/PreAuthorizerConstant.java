package com.tma.recruit.util;

import com.tma.recruit.model.enums.UserRole;

public class PreAuthorizerConstant {

    public static final String ROLE_ADMIN = "hasAuthority('ADMIN')";

    public static final String ROLE_ENGINEER = "hasRole('.ENGINEER')";

}
