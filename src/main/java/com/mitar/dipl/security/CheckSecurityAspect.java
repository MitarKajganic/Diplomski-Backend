package com.mitar.dipl.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Aspect
@Component
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

        boolean hasRole = Arrays.stream(requiredRoles)
                .anyMatch(role -> userRoles.contains("ROLE_" + role));

        if (!hasRole) {
            throw new AccessDeniedException("User does not have the required role(s): " + Arrays.toString(requiredRoles));
        }
    }
}
