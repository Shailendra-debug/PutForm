package com.skushwaha.getform.ClientApi.Controller;

import com.skushwaha.getform.Auth.UserPrincipal;
import com.skushwaha.getform.ClientApi.DTO.ApiKeyResponseDto;
import com.skushwaha.getform.ClientApi.Service.ApiKeyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;


@Controller
@AllArgsConstructor
@RequestMapping("/api/key")

public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<?> createApiKey(
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long userId = principal.getUserId();

        String apiKey = apiKeyService.create(userId);

        return ResponseEntity.ok(
                Map.of(
                        "apiKey", apiKey,
                        "message", "Store this key securely. It will not be shown again."
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponseDto>> getMyApiKeys(@AuthenticationPrincipal UserDetails userDetails) {
        List<ApiKeyResponseDto> apiKeys = apiKeyService.getUserApiKeys(userDetails.getUsername());
        return ResponseEntity.ok(apiKeys);
    }


    @PatchMapping("/{apiKeyId}/lock")
    public ResponseEntity<Void> lock(
            @PathVariable Long apiKeyId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        System.out.println("Hello");

        apiKeyService.lock(user.getUserId(), apiKeyId);
        System.out.println("hi");

        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{apiKeyId}/unlock")
    public ResponseEntity<Void> unlock(
            @PathVariable Long apiKeyId,
            @AuthenticationPrincipal UserPrincipal user
    ) {

        apiKeyService.unlock(user.getUserId(), apiKeyId);

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{apiKeyId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long apiKeyId,
            @AuthenticationPrincipal UserPrincipal user
    ) {

        apiKeyService.delete(user.getUserId(), apiKeyId);

        return ResponseEntity.noContent().build();
    }

}
