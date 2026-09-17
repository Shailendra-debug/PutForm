package com.skushwaha.getform.Forms.Controller;


import com.skushwaha.getform.Auth.UserPrincipal;
import com.skushwaha.getform.Forms.DTO.CreateFormRequest;
import com.skushwaha.getform.Forms.DTO.FormResponse;
import com.skushwaha.getform.Forms.DTO.UpdateFormRequest;
import com.skushwaha.getform.Forms.Service.FormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;


    // =========================
    // CREATE FORM
    // =========================

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FormResponse> createForm(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateFormRequest request
    ) {

        Long ownerId = principal.getUserId();

        FormResponse response =
                formService.createForm(ownerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================
    // GET MY FORMS
    // =========================

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<FormResponse>> getMyForms(
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long ownerId = principal.getUserId();

        return ResponseEntity.ok(
                formService.getMyForms(ownerId)
        );
    }


    // =========================
    // GET FORM BY ID
    // =========================

    @GetMapping("/{formId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FormResponse> getForm(
            @PathVariable UUID formId
    ) {

        return ResponseEntity.ok(
                formService.getForm(formId));
    }


    // =========================
    // UPDATE FORM
    // =========================

    @PutMapping("/{formId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FormResponse> updateForm(
            @PathVariable UUID formId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateFormRequest request
    ) {

        Long ownerId = principal.getUserId();

        return ResponseEntity.ok(
                formService.updateForm(
                        formId,
                        ownerId,
                        request
                )
        );
    }


    // =========================
    // PUBLISH FORM
    // =========================

    @PatchMapping("/{formId}/publish")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FormResponse> publishForm(
            @PathVariable UUID formId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long ownerId = principal.getUserId();

        return ResponseEntity.ok(
                formService.publishForm(
                        formId,
                        ownerId
                )
        );
    }


    // =========================
    // CLOSE FORM
    // =========================

    @PatchMapping("/{formId}/close")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FormResponse> closeForm(
            @PathVariable UUID formId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long ownerId = principal.getUserId();

        return ResponseEntity.ok(
                formService.closeForm(
                        formId,
                        ownerId
                )
        );
    }


    // =========================
    // ARCHIVE FORM
    // =========================

    @PatchMapping("/{formId}/archive")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FormResponse> archiveForm(
            @PathVariable UUID formId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long ownerId = principal.getUserId();

        return ResponseEntity.ok(
                formService.archiveForm(
                        formId,
                        ownerId
                )
        );
    }


    // =========================
    // DELETE FORM
    // =========================

    @DeleteMapping("/{formId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteForm(
            @PathVariable UUID formId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long ownerId = principal.getUserId();

        formService.deleteForm(
                formId,
                ownerId
        );

        return ResponseEntity.noContent().build();
    }
}
