package com.skushwaha.getform.Forms.DTO;


import com.skushwaha.getform.Forms.Entity.FormPlan;
import com.skushwaha.getform.Question.DTO.UpdateQuestionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFormRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "Plan is required")
    private FormPlan plan;

    @Positive(message = "Response limit must be greater than 0")
    private Long responseLimit;

    @DecimalMin(value = "0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal fillingAmount;

    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiresAt;

    @Valid
    @Size(max = 200, message = "A form can have at most 200 questions")
    private List<UpdateQuestionRequest> questionRequestList;

}