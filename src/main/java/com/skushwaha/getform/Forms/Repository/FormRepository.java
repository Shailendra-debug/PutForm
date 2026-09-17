package com.skushwaha.getform.Forms.Repository;

import com.skushwaha.getform.Forms.Entity.Form;
import com.skushwaha.getform.Forms.Entity.FormPlan;
import com.skushwaha.getform.Forms.Entity.FormStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormRepository extends JpaRepository<Form, UUID> {

    // =========================
    // Owner
    // =========================

    List<Form> findByOwnerId(Long ownerId);

    long countByOwnerId(Long ownerId);

    boolean existsByOwnerId(Long ownerId);


    // =========================
    // Owner + Status
    // =========================

    List<Form> findByOwnerIdAndStatus(
            Long ownerId,
            FormStatus status
    );

    long countByOwnerIdAndStatus(
            Long ownerId,
            FormStatus status
    );

    boolean existsByOwnerIdAndStatus(
            Long ownerId,
            FormStatus status
    );


    // =========================
    // Owner + Plan
    // =========================

    List<Form> findByOwnerIdAndPlan(
            Long ownerId,
            FormPlan plan
    );

    long countByOwnerIdAndPlan(
            Long ownerId,
            FormPlan plan
    );


    // =========================
    // ID + Owner
    // =========================

    Optional<Form> findByIdAndOwnerId(
            UUID id,
            Long ownerId
    );

    boolean existsByIdAndOwnerId(
            UUID id,
            Long ownerId
    );

    void deleteByIdAndOwnerId(
            UUID id,
            Long ownerId
    );


    // =========================
    // Title
    // =========================

    List<Form> findByTitleContainingIgnoreCase(
            String title
    );

    List<Form> findByOwnerIdAndTitleContainingIgnoreCase(
            Long ownerId,
            String title
    );


    // =========================
    // Created Date
    // =========================

    List<Form> findByOwnerIdOrderByCreatedAtDesc(
            Long ownerId
    );

    List<Form> findByOwnerIdOrderByCreatedAtAsc(
            Long ownerId
    );


    // =========================
    // Updated Date
    // =========================

    List<Form> findByOwnerIdOrderByUpdatedAtDesc(
            Long ownerId
    );


    // =========================
    // Expiration
    // =========================

    List<Form> findByExpiresAtBefore(
            LocalDateTime dateTime
    );

    List<Form> findByExpiresAtAfter(
            LocalDateTime dateTime
    );

    List<Form> findByOwnerIdAndExpiresAtBefore(
            Long ownerId,
            LocalDateTime dateTime
    );


    // =========================
    // Response Limit
    // =========================

    List<Form> findByResponseLimitLessThan(
            Long limit
    );

    List<Form> findByResponseLimitGreaterThan(
            Long limit
    );


    // =========================
    // Filling Amount
    // =========================

    List<Form> findByFillingAmountGreaterThan(
            BigDecimal amount
    );

    List<Form> findByFillingAmountLessThan(
            BigDecimal amount
    );
}