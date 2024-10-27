package com.mitar.dipl.security.oauth2;

import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.model.entity.enums.Role;
import com.mitar.dipl.repository.UserRepository;
import com.mitar.dipl.security.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final String frontendUrl;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil,
                                              @Value("${app.frontend.url}") String frontendUrl,
                                              UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.frontendUrl = frontendUrl;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        Object principal = authentication.getPrincipal();
        logger.info("Authentication Principal Class: {}", principal.getClass().getName());

        String email;

        if (principal instanceof OidcUser oidcUser) {
            // Logic for OidcUser
            email = oidcUser.getAttribute("email");

            logger.info("OAuth2SuccessHandler login successful for user: {}", email);
        } else if (principal instanceof OAuth2User oauth2User) {
            // Fallback for non-OIDC OAuth2User
            email = oauth2User.getAttribute("email");

            logger.info("OAuth2SuccessHandler login successful for user: {}", email);
        } else {
            logger.error("Principal is neither OidcUser nor OAuth2User");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed");
            return;
        }

        if (email == null) {
            logger.error("Email not found in OAuth2 response");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed");
            return;
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(email));

        // Generate JWT token
        String token = jwtUtil.generateToken(email, Role.CUSTOMER.name());

        logger.info("JWT token generated for user: {}", email);

        // Redirect to frontend with token
        String redirectUrl = UriComponentsBuilder.fromHttpUrl(frontendUrl)
                .path("/oauth2/callback")
                .queryParam("token", token)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }

    private User registerNewUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        user.setPassword("OAUTH2");

        logger.info("Registered new user: {}", email);

        return userRepository.save(user);
    }
}
