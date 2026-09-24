package com.skushwaha.getform.Public.Controller;

import com.skushwaha.getform.Auth.UserPrincipal;
import com.skushwaha.getform.Forms.DTO.FormResponse;
import com.skushwaha.getform.Forms.Service.FormService;
import com.skushwaha.getform.HomePage.DTO.ContactMessageRequest;
import com.skushwaha.getform.HomePage.DTO.ContactMessageResponse;
import com.skushwaha.getform.HomePage.Service.ContactMessageService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final FormService formService;
    private final ResponseService responseService;
    private final ContactMessageService contactMessageService;

    @GetMapping("/health")
    public String health(){
        return "Health UP";
    }

    @PostMapping("/contact")
    public ResponseEntity<Map<String, Object>> submit(
            @Valid @RequestBody ContactMessageRequest request) {

        ContactMessageResponse saved = contactMessageService.save(request);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", "Thanks! Your message has been sent. We'll reply shortly.");
        body.put("data", saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/forms/{formId}")
    public ResponseEntity<FormResponse> getForm(
            @PathVariable UUID formId
    ) {
        return ResponseEntity.ok(
                formService.getPublicForm(formId)
        );
    }

    @PostMapping("forms/{formId}/responses")
    public ResponseEntity<ResponseDto> submitResponse(
            @PathVariable UUID formId,
            @Valid @RequestBody CreateResponseRequest request) {

        ResponseDto response =
                responseService.submitResponse(formId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}