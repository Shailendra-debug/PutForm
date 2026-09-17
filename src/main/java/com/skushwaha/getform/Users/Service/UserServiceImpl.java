package com.skushwaha.getform.Users.Service;

import com.skushwaha.getform.Auth.DTO.AuthResponse;
import com.skushwaha.getform.Auth.DTO.RegisterRequest;
import com.skushwaha.getform.Auth.Service.JwtService;
import com.skushwaha.getform.Users.DTO.UpdateProfile;
import com.skushwaha.getform.Users.DTO.UserResponse;
import com.skushwaha.getform.Users.Entity.Role;
import com.skushwaha.getform.Users.Entity.User;
import com.skushwaha.getform.Users.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String usernameOrEmail) {
        User user = findEntityByUsernameOrEmail(usernameOrEmail);
        return mapToUserResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String usernameOrEmail, UpdateProfile request) {
        User user = findEntityByUsernameOrEmail(usernameOrEmail);

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }

        if (request.getProfileUrl() != null) {
            user.setProfileUrl(request.getProfileUrl().trim());
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponseDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public User findEntityByUsernameOrEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + email));
    }

    // --- Helper Mapper Method ---
    private UserResponse mapToUserResponseDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .profileUrl(user.getProfileUrl())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .walletBalance(user.getWalletBalance())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}