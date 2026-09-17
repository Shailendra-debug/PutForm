package com.skushwaha.getform.ClientApi.Service;

import com.skushwaha.getform.ClientApi.Config.ApiKeyGenerator;
import com.skushwaha.getform.ClientApi.DTO.ApiKeyResponseDto;
import com.skushwaha.getform.ClientApi.Entity.ApiKey;
import com.skushwaha.getform.ClientApi.Repository.ApiKeyRepository;
import com.skushwaha.getform.Users.Entity.User;
import com.skushwaha.getform.Users.Repository.UserRepository;
import com.skushwaha.getform.Users.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyGenerator generator;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public String create(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found"));

        String rawKey = generator.generate();

        String hash = encryptionService.encrypt(rawKey);

        ApiKey apiKey = new ApiKey();

        apiKey.setKeyHash(hash);
        apiKey.setKeyPrefix(rawKey.substring(0, 8));
        apiKey.setUser(user);
        apiKey.setActive(true);
        apiKey.setCreatedAt(LocalDateTime.now());
        apiKey.setLastUsedAt(LocalDateTime.now());

        apiKeyRepository.save(apiKey);

        return rawKey;
    }

    @Transactional
    public List<ApiKeyResponseDto> getUserApiKeys(String usernameOrEmail) {
        // Fetch the user entity first
        User user = userService.findEntityByUsernameOrEmail(usernameOrEmail);

        // Fetch all API keys for this user and map to DTOs
        return apiKeyRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToApiKeyResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ApiKeyResponseDto> getApiKeysByUserId(Long userId) {
        return apiKeyRepository.findByUserId(userId)
                .stream()
                .map(this::mapToApiKeyResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void lock(Long userId, Long apiKeyId) {

        ApiKey apiKey = apiKeyRepository
                .findByIdAndUserId(apiKeyId, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("API key not found"));

        apiKey.setActive(false);

        apiKeyRepository.save(apiKey);
    }

    @Transactional
    public void unlock(Long userId, Long apiKeyId) {

        ApiKey apiKey = apiKeyRepository
                .findByIdAndUserId(apiKeyId, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("API key not found"));

        apiKey.setActive(true);

        apiKeyRepository.save(apiKey);
    }

    @Transactional
    public void delete(Long userId, Long apiKeyId) {

        ApiKey apiKey = apiKeyRepository
                .findByIdAndUserId(apiKeyId, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("API key not found"));

        apiKeyRepository.delete(apiKey);
    }



    // --- Helper Mapper Method ---
    private ApiKeyResponseDto mapToApiKeyResponseDto(ApiKey apiKey) {
        return ApiKeyResponseDto.builder()
                .id(apiKey.getId())
                .keyPrefix(encryptionService.decrypt(apiKey.getKeyHash()))
                .active(apiKey.isActive())
                .lastUsedAt(apiKey.getLastUsedAt())
                .createdAt(apiKey.getCreatedAt())
                .expiresAt(apiKey.getExpiresAt())
                .build();
    }
}
