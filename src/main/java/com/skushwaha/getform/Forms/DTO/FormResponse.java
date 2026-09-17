package com.skushwaha.getform.Forms.DTO;

import com.skushwaha.getform.Forms.Entity.FormPlan;
import com.skushwaha.getform.Forms.Entity.FormStatus;
import com.skushwaha.getform.Question.DTO.CreateQuestionRequest;
import com.skushwaha.getform.Question.DTO.QuestionResponse;
import jakarta.validation.Valid;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormResponse {

    private UUID id;

    private Long ownerId;

    private String title;

    private String description;

    private FormStatus status;

    private FormPlan plan;

    private Long responseLimit;

    private BigDecimal fillingAmount;

    private BigDecimal totalCollectionAmount;

    @Valid
    private List<QuestionResponse> questions;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
