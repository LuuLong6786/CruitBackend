package com.tma.recruit.anotation;

import com.tma.recruit.util.PreAuthorizerConstant;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(PreAuthorizerConstant.USER_ROLE)
public @interface OnlyUser {
}
