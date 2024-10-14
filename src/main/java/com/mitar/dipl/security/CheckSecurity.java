package com.mitar.dipl.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("@securityChecker.checkAccess(authentication, #roles)")
public @interface CheckSecurity {
    String[] roles();
}
