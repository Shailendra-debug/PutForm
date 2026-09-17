package com.skushwaha.getform.Question.Controller;

import com.skushwaha.getform.Question.DTO.CreateQuestionRequest;
import com.skushwaha.getform.Question.DTO.QuestionResponse;
import com.skushwaha.getform.Question.DTO.UpdateQuestionRequest;
import com.skushwaha.getform.Question.Service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/forms/{formId}/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;


    // =========================
    // CREATE QUESTION
    // =========================

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(
            @PathVariable UUID formId,
            @Valid @RequestBody CreateQuestionRequest request
    ) {

        QuestionResponse response =
                questionService.createQuestion(
                        formId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================
    // GET ALL QUESTIONS
    // =========================

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @PathVariable UUID formId
    ) {

        return ResponseEntity.ok(
                questionService.getQuestionsByForm(formId)
        );
    }


    // =========================
    // GET QUESTION BY ID
    // =========================

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestion(
            @PathVariable UUID formId,
            @PathVariable UUID questionId
    ) {

        return ResponseEntity.ok(
                questionService.getQuestion(
                        formId,
                        questionId
                )
        );
    }


    // =========================
    // UPDATE QUESTION
    // =========================

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable UUID formId,
            @PathVariable UUID questionId,
            @Valid @RequestBody UpdateQuestionRequest request
    ) {

        return ResponseEntity.ok(
                questionService.updateQuestion(
                        formId,
                        questionId,
                        request
                )
        );
    }


    // =========================
    // DELETE QUESTION
    // =========================

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable UUID formId,
            @PathVariable UUID questionId
    ) {

        questionService.deleteQuestion(
                formId,
                questionId
        );

        return ResponseEntity.noContent().build();
    }
}
