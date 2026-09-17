package com.skushwaha.getform.Question.Service;


import com.skushwaha.getform.Exception.ResourceNotFoundException;
import com.skushwaha.getform.Forms.Entity.Form;
import com.skushwaha.getform.Question.DTO.CreateQuestionRequest;
import com.skushwaha.getform.Question.DTO.QuestionResponse;
import com.skushwaha.getform.Question.DTO.UpdateQuestionRequest;
import com.skushwaha.getform.Question.Entity.Question;
import com.skushwaha.getform.Question.Repository.QuestionRepository;
import com.skushwaha.getform.Forms.Repository.FormRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {


    private final QuestionRepository questionRepository;
    private final FormRepository formRepository;


    // =========================
    // CREATE QUESTION
    // =========================



    @Override
    public QuestionResponse createQuestion(
            UUID formId,
            CreateQuestionRequest request
    ) {

        // Check form exists
        if (!formRepository.existsById(formId)) {
            throw new ResourceNotFoundException("Form not found");
        }

        Optional<Form> form=formRepository.findById(formId);



        Question savedQuestion =
                questionRepository.save(mapToEntety(request,form.get()));

        return mapToResponse(savedQuestion);
    }

    @Override
    public List<QuestionResponse> createQuestionList(
            UUID formId,
            List<CreateQuestionRequest> request) {

        Optional<Form> form=formRepository.findById(formId);
        List<Question> list = request.stream()
                .map(i -> mapToEntety(i, form.get()))
                .toList();

        return questionRepository.saveAll(list)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================
    // GET QUESTIONS
    // =========================

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByForm(
            UUID formId
    ) {

        if (!formRepository.existsById(formId)) {
            throw new ResourceNotFoundException("Form not found");
        }

        return questionRepository
                .findByFormIdOrderByQuestionOrderAsc(formId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================
    // GET QUESTION
    // =========================

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getQuestion(
            UUID formId,
            UUID questionId
    ) {

        Question question =
                getQuestionFromForm(formId, questionId);

        return mapToResponse(question);
    }


    // =========================
    // UPDATE QUESTION
    // =========================

    @Override
    public QuestionResponse updateQuestion(
            UUID formId,
            UUID questionId,
            UpdateQuestionRequest request
    ) {

        Question question =
                getQuestionFromForm(formId, questionId);

        question.setQuestion(request.getQuestion());
        question.setType(request.getType());
        question.setRequired(request.getRequired());
        question.setQuestionOrder(request.getQuestionOrder());
        question.setConfiguration(request.getConfiguration());

        Question updatedQuestion =
                questionRepository.save(question);

        return mapToResponse(updatedQuestion);
    }


    // =========================
    // DELETE QUESTION
    // =========================

    @Override
    public void deleteQuestion(
            UUID formId,
            UUID questionId
    ) {

        Question question =
                getQuestionFromForm(formId, questionId);

        questionRepository.delete(question);
    }


    // =========================
    // FIND QUESTION
    // =========================

    private Question getQuestionFromForm(
            UUID formId,
            UUID questionId
    ) {

        return questionRepository
                .findByIdAndFormId(questionId, formId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Question not found"
                        )
                );
    }


    // =========================
    // ENTITY → DTO
    // =========================

    private QuestionResponse mapToResponse(
            Question question
    ) {

        return QuestionResponse.builder()
                .id(question.getId())
                .formId(question.getForm().getId())
                .question(question.getQuestion())
                .type(question.getType())
                .required(question.getRequired())
                .questionOrder(question.getQuestionOrder())
                .configuration(question.getConfiguration())
                .build();
    }

    private Question mapToEntety(CreateQuestionRequest request, Form formId){

        Question question = Question.builder()
                .form(formId)
                .question(request.getQuestion())
                .type(request.getType())
                .required(request.getRequired())
                .questionOrder(request.getQuestionOrder())
                .configuration(request.getConfiguration())
                .build();

        return question;

    }
}
