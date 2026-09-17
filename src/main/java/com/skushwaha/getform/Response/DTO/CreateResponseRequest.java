package com.skushwaha.getform.Response.DTO;

import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateResponseRequest {

    @Email(message = "Invalid email address")
    private String respondentEmail;

    private Map<String, Object> answers;
}
