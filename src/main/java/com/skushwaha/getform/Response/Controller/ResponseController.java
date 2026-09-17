package com.skushwaha.getform.Response.Controller;

import com.skushwaha.getform.Response.DTO.CreateResponseRequest;
import com.skushwaha.getform.Response.DTO.ResponseDto;
import com.skushwaha.getform.Response.Service.ResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/forms/{formId}/responses")
@RequiredArgsConstructor
public class ResponseController {

    private final ResponseService responseService;

    // PUBLIC - anyone can submit a response
    @PostMapping
    public ResponseEntity<ResponseDto> submitResponse(
            @PathVariable UUID formId,
            @Valid @RequestBody CreateResponseRequest request) {

        ResponseDto response =
                responseService.submitResponse(formId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // USER or ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{responseId}")
    public ResponseEntity<ResponseDto> getResponse(
            @PathVariable UUID formId,
            @PathVariable Long responseId) {

        return ResponseEntity.ok(
                responseService.getResponse(formId, responseId)
        );
    }

    // USER or ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<ResponseDto>> getFormResponses(
            @PathVariable UUID formId) {

        return ResponseEntity.ok(
                responseService.getFormResponses(formId)
        );
    }

    // USER or ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/payments/pending")
    public ResponseEntity<List<ResponseDto>> getPendingPayments(
            @PathVariable UUID formId) {

        return ResponseEntity.ok(
                responseService.getPendingPayments(formId)
        );
    }

    // USER or ADMIN
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/payments/success")
    public ResponseEntity<List<ResponseDto>> getSuccessfulPayments(
            @PathVariable UUID formId) {

        return ResponseEntity.ok(
                responseService.getSuccessfulPayments(formId)
        );
    }

    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{responseId}/payment-status")
    public ResponseEntity<ResponseDto> updatePaymentStatus(
            @PathVariable UUID formId,
            @PathVariable Long responseId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                responseService.updatePaymentStatus(
                        formId,
                        responseId,
                        status
                )
        );
    }

    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{responseId}")
    public ResponseEntity<Void> deleteResponse(
            @PathVariable UUID formId,
            @PathVariable Long responseId) {

        responseService.deleteResponse(formId, responseId);

        return ResponseEntity.noContent().build();
    }
}