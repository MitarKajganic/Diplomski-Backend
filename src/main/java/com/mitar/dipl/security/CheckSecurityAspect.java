package com.mitar.dipl.security;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class CheckSecurityAspect {

    @Before("@annotation(checkSecurity)")
    public void checkSecurity(JoinPoint joinPoint, CheckSecurity checkSecurity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Collection<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String[] requiredRoles = checkSecurity.roles();

        List<String> requiredRolesList = Arrays.stream(requiredRoles[0].split(","))
                .map(String::trim)
                .toList();

        boolean hasRole = requiredRolesList.stream()
                .anyMatch(role -> userRoles.contains("ROLE_" + role));

        if (!hasRole) {
            throw new AccessDeniedException("User does not have the required role(s): " + Arrays.toString(requiredRoles));
        }
    }
}
