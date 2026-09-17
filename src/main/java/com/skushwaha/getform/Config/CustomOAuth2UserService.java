package com.skushwaha.getform.Config;

import com.skushwaha.getform.Users.Entity.Role;
import com.skushwaha.getform.Users.Entity.User;
import com.skushwaha.getform.Users.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class CustomOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        DefaultOAuth2UserService delegate =
                new DefaultOAuth2UserService();

        OAuth2User oauth2User = delegate.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        User user = userRepository
                .findByEmail(email)
                .orElseGet(() -> {

                    User newUser = new User();

                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setRole(Role.USER);

                    return userRepository.save(newUser);
                });

        Set<GrantedAuthority> authorities = new HashSet<>(
                oauth2User.getAuthorities()
        );

        authorities.add(
                new SimpleGrantedAuthority(user.getRole().name())
        );

        return new DefaultOAuth2User(
                authorities,
                oauth2User.getAttributes(),
                "email"
        );
    }
}
