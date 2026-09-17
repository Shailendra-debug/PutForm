package com.skushwaha.getform.Forms.Service;

import com.skushwaha.getform.Exception.ResourceNotFoundException;
import com.skushwaha.getform.Exception.UnauthorizedException;
import com.skushwaha.getform.Forms.DTO.CreateFormRequest;
import com.skushwaha.getform.Forms.DTO.FormResponse;
import com.skushwaha.getform.Forms.DTO.UpdateFormRequest;
import com.skushwaha.getform.Forms.Entity.Form;
import com.skushwaha.getform.Forms.Entity.FormPlan;
import com.skushwaha.getform.Forms.Entity.FormStatus;
import com.skushwaha.getform.Forms.Repository.FormRepository;

import com.skushwaha.getform.Question.DTO.QuestionResponse;
import com.skushwaha.getform.Question.DTO.UpdateQuestionRequest;
import com.skushwaha.getform.Question.Entity.Question;
import com.skushwaha.getform.Question.Repository.QuestionRepository;
import com.skushwaha.getform.Question.Service.QuestionService;
import com.skushwaha.getform.Response.Entity.PaymentStatus;
import com.skushwaha.getform.Response.Repository.ResponseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;
    private final QuestionService questionService;
    private final ResponseRepository responseRepository;
    private final QuestionRepository questionRepository;
    private final PublicFormRedisService publicFormRedisService;

    // =========================
    // CREATE
    // =========================

    @Override
    public FormResponse createForm(
            Long ownerId,
            CreateFormRequest request
    ) {

        Form form = Form.builder()
                .ownerId(ownerId)
                .title(request.getTitle())
                .description(request.getDescription())
                .responseLimit(request.getResponseLimit())
                .fillingAmount(request.getFillingAmount())
                .expiresAt(request.getExpiresAt())
                .status(FormStatus.DRAFT)
                .build();
        if (request.getPlan()== FormPlan.FREE){

            if (request.getResponseLimit()<500){
                form.setResponseLimit(request.getResponseLimit());
            }else {
                form.setResponseLimit(500L);
            }

        }else if (request.getPlan()==FormPlan.BASIC){
            if (request.getResponseLimit()<1500){
                form.setResponseLimit(request.getResponseLimit());
            }else {
                form.setResponseLimit(1500L);
            }
        }else {

            if (request.getResponseLimit()<5000){
                form.setResponseLimit(request.getResponseLimit());
            }else {
                form.setResponseLimit(5000L);
            }

        }

        Form savedForm = formRepository.save(form);

        List<QuestionResponse>savedQue=questionService.createQuestionList(savedForm.getId(), request.getQuestionRequestSet());

        FormResponse finalResponse=mapToResponse(savedForm);

        finalResponse.setQuestions(savedQue);

        return finalResponse;
    }


    // =========================
    // GET BY ID
    // =========================

    @Override
    @Transactional(readOnly = true)
    public FormResponse getForm(
            UUID formId
    ) {

        Optional<Form> form = formRepository.findById(formId);

        List<QuestionResponse>list=questionService.getQuestionsByForm(formId);

        assert form.orElse(null) != null;
        FormResponse finalResponse=mapToResponse(form.orElse(null));

        finalResponse.setQuestions(list);

        long size=responseRepository.countByFormIdAndPaymentStatus(formId, PaymentStatus.SUCCESS);

        finalResponse.setTotalCollectionAmount(form.get().getFillingAmount().multiply(BigDecimal.valueOf(size)));

        return finalResponse;
    }

    @Override
    public FormResponse getPublicForm(UUID formId) {

        // ==========================================
        // 1. CHECK REDIS
        // ==========================================

        FormResponse cached =
                publicFormRedisService.get(formId);

        if (cached != null) {
            return cached;
        }


        // ==========================================
        // 2. GET FROM DATABASE
        // ==========================================

        Form form =
                formRepository.findById(formId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Form not found"
                                )
                        );

        if (form.getStatus()!=FormStatus.PUBLISHED)throw new  UnauthorizedException("Form Are "+form.getStatus());


        // ==========================================
        // 3. GET QUESTIONS
        // ==========================================

        List<QuestionResponse> questions =
                questionService.getQuestionsByForm(formId);


        // ==========================================
        // 4. MAP RESPONSE
        // ==========================================

        FormResponse response =
                mapToResponse(form);

        response.setQuestions(questions);


        // ==========================================
        // 5. CALCULATE COLLECTION
        // ==========================================

        long successfulResponses =
                responseRepository
                        .countByFormIdAndPaymentStatus(
                                formId,
                                PaymentStatus.SUCCESS
                        );

        BigDecimal totalCollection =
                form.getFillingAmount()
                        .multiply(
                                BigDecimal.valueOf(
                                        successfulResponses
                                )
                        );

        response.setTotalCollectionAmount(
                totalCollection
        );


        // ==========================================
        // 6. SAVE TO REDIS
        // ==========================================

        publicFormRedisService.save(
                formId,
                response
        );


        return response;
    }


    // =========================
    // GET MY FORMS
    // =========================

    @Override
    @Transactional(readOnly = true)
    public List<FormResponse> getMyForms(
            Long ownerId
    ) {

        return formRepository
                .findByOwnerId(ownerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================
    // UPDATE
    // =========================
    @Transactional
    @Override
    public FormResponse updateForm(
            UUID formId,
            Long ownerId,
            UpdateFormRequest request
    ) {

        // -------------------------
        // GET FORM
        // -------------------------
        if (publicFormRedisService.get(formId)!=null)publicFormRedisService.delete(formId);

        Form form = formRepository.findById(formId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Form not found")
                );


        // -------------------------
        // OWNER CHECK
        // -------------------------

        if (!Objects.equals(form.getOwnerId(), ownerId)) {
            throw new UnauthorizedException(
                    "You are not Form owner"
            );
        }


        // -------------------------
        // UPDATE FORM FIELDS
        // -------------------------

        form.setTitle(request.getTitle());
        form.setDescription(request.getDescription());
        form.setPlan(request.getPlan());
        form.setResponseLimit(request.getResponseLimit());
        form.setFillingAmount(request.getFillingAmount());
        form.setExpiresAt(request.getExpiresAt());


        // -------------------------
        // CURRENT QUESTIONS
        // -------------------------

        List<Question> currentQuestions = form.getQuestions();


        // -------------------------
        // INCOMING EXISTING IDs
        // -------------------------

        Set<UUID> incomingIds = request
                .getQuestionRequestList()
                .stream()
                .map(UpdateQuestionRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());


        // -------------------------
        // DELETE REMOVED QUESTIONS
        // -------------------------

        currentQuestions.removeIf(question ->
                question.getId() != null &&
                        !incomingIds.contains(question.getId())
        );


        // -------------------------
        // MAP EXISTING QUESTIONS
        // -------------------------

        Map<UUID, Question> existingQuestions =
                currentQuestions.stream()
                        .filter(question ->
                                question.getId() != null
                        )
                        .collect(Collectors.toMap(
                                Question::getId,
                                question -> question
                        ));


        // -------------------------
        // UPDATE / CREATE QUESTIONS
        // -------------------------

        for (UpdateQuestionRequest dto :
                request.getQuestionRequestList()) {

            Question question;


            // EXISTING QUESTION
            if (dto.getId() != null) {

                question = existingQuestions.get(dto.getId());

                if (question == null) {
                    throw new EntityNotFoundException(
                            "Question not found: " + dto.getId()
                    );
                }

            }

            // NEW QUESTION
            else {

                question = new Question();

                question.setForm(form);

                currentQuestions.add(question);
            }


            // -------------------------
            // UPDATE QUESTION FIELDS
            // -------------------------

            question.setQuestion(dto.getQuestion());

            question.setType(dto.getType());

            question.setRequired(dto.getRequired());

            question.setQuestionOrder(
                    dto.getQuestionOrder()
            );

            question.setConfiguration(
                    dto.getConfiguration() != null
                            ? new HashMap<>(dto.getConfiguration())
                            : new HashMap<>()
            );
        }


        // -------------------------
        // NO save()
        // -------------------------
        //
        // form is already managed because
        // this method is @Transactional.
        //
        // Hibernate automatically flushes
        // the changes.

        return mapToResponse(form);
    }

    // =========================
    // PUBLISH
    // =========================

    @Override
    public FormResponse publishForm(
            UUID formId,
            Long ownerId
    ) {

        Form form = getOwnedForm(formId, ownerId);

        if (form.getStatus() == FormStatus.ARCHIVED) {
            throw new IllegalStateException(
                    "Archived form cannot be published"
            );
        }

        if (form.getStatus() == FormStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Form is already published"
            );
        }

        form.setStatus(FormStatus.PUBLISHED);

        return mapToResponse(
                formRepository.save(form)
        );
    }


    // =========================
    // CLOSE
    // =========================

    @Override
    public FormResponse closeForm(
            UUID formId,
            Long ownerId
    ) {

        Form form = getOwnedForm(formId, ownerId);
        publicFormRedisService.delete(formId);

        publicFormRedisService.delete(formId);

        if (form.getStatus() != FormStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Only published forms can be closed"
            );
        }

        form.setStatus(FormStatus.CLOSED);

        return mapToResponse(
                formRepository.save(form)
        );
    }


    // =========================
    // ARCHIVE
    // =========================

    @Override
    public FormResponse archiveForm(
            UUID formId,
            Long ownerId
    ) {

        Form form = getOwnedForm(formId, ownerId);

        publicFormRedisService.delete(formId);

        form.setStatus(FormStatus.ARCHIVED);

        return mapToResponse(
                formRepository.save(form)
        );
    }


    // =========================
    // DELETE
    // =========================

    @Override
    public void deleteForm(
            UUID formId,
            Long ownerId
    ) {

        Form form = getOwnedForm(formId, ownerId);

        formRepository.delete(form);
    }


    // =========================
    // FIND OWNER FORM
    // =========================

    private Form getOwnedForm(
            UUID formId,
            Long ownerId
    ) {

        return formRepository
                .findByIdAndOwnerId(formId, ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Form not found"
                        )
                );
    }


    // =========================
    // ENTITY → DTO
    // =========================

    private FormResponse mapToResponse(
            Form form
            ) {

        return FormResponse.builder()
                .id(form.getId())
                .ownerId(form.getOwnerId())
                .title(form.getTitle())
                .description(form.getDescription())
                .status(form.getStatus())
                .plan(form.getPlan())
                .responseLimit(form.getResponseLimit())
                .fillingAmount(form.getFillingAmount())
                .expiresAt(form.getExpiresAt())
                .createdAt(form.getCreatedAt())
                .updatedAt(form.getUpdatedAt())
                .build();
    }

    private Question mapToEntetyForQuestion(UpdateQuestionRequest request, Form formId){

        if (request.getId().equals(null)){
            Question question = Question.builder()
                    .id(request.getId())
                    .question(request.getQuestion())
                    .type(request.getType())
                    .required(request.getRequired())
                    .questionOrder(request.getQuestionOrder())
                    .configuration(request.getConfiguration())
                    .build();
            return question;
        }

        Question question = Question.builder()
                .form(formId)
                .id(request.getId())
                .question(request.getQuestion())
                .type(request.getType())
                .required(request.getRequired())
                .questionOrder(request.getQuestionOrder())
                .configuration(request.getConfiguration())
                .build();

        return question;

    }
}