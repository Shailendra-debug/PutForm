package com.skushwaha.getform.Question.DTO;

import com.skushwaha.getform.Question.Entity.QuestionType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuestionRequest {

    @NotBlank(message = "Question is required")
    @Size(max = 500, message = "Question must not exceed 500 characters")
    private String question;

    @NotNull(message = "Question type is required")
    private QuestionType type;

    @NotNull(message = "Required field is required")
    @Builder.Default
    private Boolean required = false;

    @NotNull(message = "Question order is required")
    @Positive(message = "Question order must be greater than 0")
    private Integer questionOrder;

    private Map<String, Object> configuration;
}