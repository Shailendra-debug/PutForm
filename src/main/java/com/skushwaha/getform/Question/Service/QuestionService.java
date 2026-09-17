package com.skushwaha.getform.Question.Service;


import com.skushwaha.getform.Question.DTO.CreateQuestionRequest;
import com.skushwaha.getform.Question.DTO.QuestionResponse;
import com.skushwaha.getform.Question.DTO.UpdateQuestionRequest;

import java.util.List;
import java.util.UUID;

public interface QuestionService {

    QuestionResponse createQuestion(
            UUID formId,
            CreateQuestionRequest request
    );

    List<QuestionResponse> createQuestionList(
            UUID formId,
            List<CreateQuestionRequest> request
    );

    List<QuestionResponse> getQuestionsByForm(
            UUID formId
    );

    QuestionResponse getQuestion(
            UUID formId,
            UUID questionId
    );

    QuestionResponse updateQuestion(
            UUID formId,
            UUID questionId,
            UpdateQuestionRequest request
    );

    void deleteQuestion(
            UUID formId,
            UUID questionId
    );
}
