package com.skushwaha.getform.Forms.DTO;



import com.skushwaha.getform.Forms.Entity.FormPlan;
import com.skushwaha.getform.Question.DTO.CreateQuestionRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import jakarta.validation.Valid;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFormRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Plan is required")
    private FormPlan plan;

    @Positive(message = "Response limit must be greater than 0")
    private Long responseLimit;

    @DecimalMin(value = "0.00", message = "Filling amount cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Invalid amount")
    @Builder.Default
    private BigDecimal fillingAmount = BigDecimal.ZERO;

    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiresAt;

    @Valid
    private List<CreateQuestionRequest> questionRequestSet;


}
