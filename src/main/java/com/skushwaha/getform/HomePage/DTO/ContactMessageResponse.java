package com.skushwaha.getform.HomePage.DTO;




import com.skushwaha.getform.HomePage.Entity.ContactStatus;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class ContactMessageResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String subject;
    private String message;
    private ContactStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



}
