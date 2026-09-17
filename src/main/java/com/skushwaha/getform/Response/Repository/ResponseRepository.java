package com.skushwaha.getform.Response.Repository;

import com.skushwaha.getform.Response.Entity.PaymentStatus;
import com.skushwaha.getform.Response.Entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResponseRepository extends JpaRepository<Response, Long> {

    // Get all responses of a form
    List<Response> findByFormId(Long formId);

    // Get responses latest first
    List<Response> findByFormIdOrderBySubmittedAtDesc(UUID formId);

    // Count total responses
    long countByFormId(UUID formId);

    // Check duplicate submission
    boolean existsByFormIdAndRespondentEmail(
            UUID formId,
            String respondentEmail
    );

    // Find response by ID and form ID
    Optional<Response> findByIdAndFormId(
            Long id,
            UUID formId
    );

    // Get responses by payment status
    List<Response> findByFormIdAndPaymentStatus(
            UUID formId,
            PaymentStatus paymentStatus
    );

    // Count responses by payment status
    long countByFormIdAndPaymentStatus(
            UUID formId,
            PaymentStatus paymentStatus
    );

    // Find a response by ID and payment status
    Optional<Response> findByIdAndPaymentStatus(
            Long id,
            PaymentStatus paymentStatus
    );
}