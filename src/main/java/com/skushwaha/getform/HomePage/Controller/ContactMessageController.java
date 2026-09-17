package com.skushwaha.getform.HomePage.Controller;


import com.skushwaha.getform.HomePage.DTO.ContactMessageRequest;
import com.skushwaha.getform.HomePage.DTO.ContactMessageResponse;
import com.skushwaha.getform.HomePage.Entity.ContactStatus;
import com.skushwaha.getform.HomePage.Service.ContactMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService service;



    /* ---------- PUBLIC — Submit contact form ---------- */

    @PostMapping
    public ResponseEntity<Map<String, Object>> submit(
            @Valid @RequestBody ContactMessageRequest request) {

        ContactMessageResponse saved = service.save(request);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", "Thanks! Your message has been sent. We'll reply shortly.");
        body.put("data", saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /* ---------- ADMIN — List all messages ---------- */

    @GetMapping
    public ResponseEntity<List<ContactMessageResponse>> listAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /* ---------- ADMIN — List by status ---------- */

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ContactMessageResponse>> listByStatus(
            @PathVariable ContactStatus status) {
        return ResponseEntity.ok(service.findByStatus(status));
    }

    /* ---------- ADMIN — Get one ---------- */

    @GetMapping("/{id}")
    public ResponseEntity<ContactMessageResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /* ---------- ADMIN — Update status ---------- */

    @PatchMapping("/{id}/status")
    public ResponseEntity<ContactMessageResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam ContactStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    /* ---------- ADMIN — Delete ---------- */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}