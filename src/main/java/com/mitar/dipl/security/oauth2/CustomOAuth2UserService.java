package com.mitar.dipl.security.oauth2;

import com.mitar.dipl.model.entity.User;
import com.mitar.dipl.model.entity.enums.Role;
import com.mitar.dipl.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google"
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); // "sub"

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        logger.info("OAuth2 login successful for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(email, name, picture));

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(String email, String name, String picture) {
        User user = new User();
        user.setEmail(email);
//        user.setName(name);
//        user.setPicture(picture);
//        user.setProvider("google");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        // Hash a default password or handle appropriately
        user.setPassword("N/A"); // Not applicable for OAuth2 users

        logger.info("Registered new user: {}", email);

        return userRepository.save(user);
    }
}
