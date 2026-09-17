package com.skushwaha.getform.ClientApi.Controller;

import com.skushwaha.getform.Auth.UserPrincipal;
import com.skushwaha.getform.Forms.DTO.CreateFormRequest;
import com.skushwaha.getform.Forms.DTO.FormResponse;
import com.skushwaha.getform.Forms.DTO.UpdateFormRequest;
import com.skushwaha.getform.Forms.Service.FormService;
import com.skushwaha.getform.Response.DTO.CreateResponseRequest;
import com.skushwaha.getform.Response.DTO.ResponseDto;
import com.skushwaha.getform.Response.Service.ResponseService;
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
@RequestMapping("/api/v1/forms")
@RequiredArgsConstructor
public class ApiFormController {

    private final FormService formService;
    private final ResponseService responseService;


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

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<FormResponse>> getMyForms(
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        Long ownerId = principal.getUserId();

        return ResponseEntity.ok(
                formService.getMyForms(ownerId)
        );
    }

    @PostMapping("/submit")
    public ResponseEntity<ResponseDto> submitResponse(
            @RequestParam UUID formId,
            @Valid @RequestBody CreateResponseRequest request) {

        ResponseDto response =
                responseService.submitResponse(formId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================
    // GET FORM BY ID
    // =========================

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FormResponse> getForm(
            @RequestParam UUID formId
    ) {


        return ResponseEntity.ok(
                formService.getForm(formId)
        );
    }


    // =========================
    // UPDATE FORM
    // =========================

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FormResponse> updateForm(
            @RequestParam UUID formId,
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

    @PatchMapping("/publish")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FormResponse> publishForm(
            @RequestParam UUID formId,
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

    @PatchMapping("/close")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FormResponse> closeForm(
            @RequestParam UUID formId,
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

    @PatchMapping("/archive")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FormResponse> archiveForm(
            @RequestParam UUID formId,
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

    @DeleteMapping
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
