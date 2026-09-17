package com.skushwaha.getform.Response.DTO;

import com.skushwaha.getform.Response.Entity.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto {

    private UUID id;

    private UUID formId;

    private String respondentEmail;

    private Map<String, Object> answers;

   // private PaymentStatus paymentStatus;

    private LocalDateTime submittedAt;
}
