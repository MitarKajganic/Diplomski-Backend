package com.mitar.dipl.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component("securityChecker")
public class SecurityChecker {

    /**
     * Checks if the authenticated user has at least one of the specified roles.
     *
     * @param authentication The authentication object.
     * @param roles          The roles to check against.
     * @return true if the user has at least one role, false otherwise.
     */
    public boolean checkAccess(Authentication authentication, String[] roles) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Arrays.stream(roles)
                .map(role -> "ROLE_" + role.toUpperCase())
                .anyMatch(userRoles::contains);
    }
}
