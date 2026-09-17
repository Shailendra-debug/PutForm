package com.skushwaha.getform.HomePage.Service;

import com.skushwaha.getform.HomePage.DTO.ContactMessageRequest;
import com.skushwaha.getform.HomePage.DTO.ContactMessageResponse;
import com.skushwaha.getform.HomePage.Entity.ContactMessage;
import com.skushwaha.getform.HomePage.Entity.ContactStatus;
import com.skushwaha.getform.HomePage.Repository.ContactMessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository repository;

    /* ---------- Create ---------- */

    @Transactional
    public ContactMessageResponse save(ContactMessageRequest request) {

        ContactMessage entity = new ContactMessage();
        entity.setFirstName(request.getFirstName().trim());
        entity.setLastName(request.getLastName().trim());
        entity.setEmail(request.getEmail().trim().toLowerCase());
        entity.setPhone(request.getPhone() == null ? null : request.getPhone().trim());
        entity.setSubject(request.getSubject().trim());
        entity.setMessage(request.getMessage().trim());
        entity.setStatus(ContactStatus.NEW);

        ContactMessage saved = repository.save(entity);
        return toResponse(saved);
    }

    /* ---------- Read ---------- */

    @Transactional
    public List<ContactMessageResponse> findAll() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContactMessageResponse findById(Long id) {
        ContactMessage entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contact message not found with id " + id));
        return toResponse(entity);
    }

    @Transactional
    public List<ContactMessageResponse> findByStatus(ContactStatus status) {
        return repository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /* ---------- Update Status ---------- */

    @Transactional
    public ContactMessageResponse updateStatus(Long id, ContactStatus status) {
        ContactMessage entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contact message not found with id " + id));
        entity.setStatus(status);
        return toResponse(repository.save(entity));
    }

    /* ---------- Delete ---------- */

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Contact message not found with id " + id);
        }
        repository.deleteById(id);
    }

    /* ---------- Mapper ---------- */

    private ContactMessageResponse toResponse(ContactMessage entity) {
        return new ContactMessageResponse(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getSubject(),
                entity.getMessage(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}