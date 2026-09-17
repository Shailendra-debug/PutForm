package com.skushwaha.getform.Question.DTO;

import com.skushwaha.getform.Question.Entity.QuestionType;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private UUID id;

    private UUID formId;

    private String question;

    private QuestionType type;

    private Boolean required;

    private Integer questionOrder;

    private Map<String, Object> configuration;
}
