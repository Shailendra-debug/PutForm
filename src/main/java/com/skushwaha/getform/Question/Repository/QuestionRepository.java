package com.skushwaha.getform.Question.Repository;

import com.skushwaha.getform.Question.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

    // Get all questions of a form
    List<Question> findByFormIdOrderByQuestionOrderAsc(UUID formId);

    // Get one question belonging to a specific form
    Optional<Question> findByIdAndFormId(UUID id, UUID formId);

    // Check whether question belongs to form
    boolean existsByIdAndFormId(UUID id, UUID formId);

    // Delete all questions of a form
    void deleteByFormId(UUID formId);
}
