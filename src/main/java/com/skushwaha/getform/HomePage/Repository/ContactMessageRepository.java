package com.skushwaha.getform.HomePage.Repository;

import com.skushwaha.getform.HomePage.Entity.ContactMessage;
import com.skushwaha.getform.HomePage.Entity.ContactStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    List<ContactMessage> findAllByOrderByCreatedAtDesc();

    List<ContactMessage> findByStatusOrderByCreatedAtDesc(ContactStatus status);

    boolean existsByEmailAndSubjectAndMessage(String email, String subject, String message);
}