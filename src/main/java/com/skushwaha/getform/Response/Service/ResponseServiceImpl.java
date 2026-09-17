package com.skushwaha.getform.Response.Service;

import com.skushwaha.getform.Exception.ResourceNotFoundException;
import com.skushwaha.getform.Exception.UserAlreadyExistsException;
import com.skushwaha.getform.Question.DTO.QuestionResponse;
import com.skushwaha.getform.Question.Service.QuestionService;
import com.skushwaha.getform.Response.DTO.CreateResponseRequest;
import com.skushwaha.getform.Response.DTO.ResponseDto;
import com.skushwaha.getform.Response.Entity.PaymentStatus;
import com.skushwaha.getform.Response.Entity.Response;
import com.skushwaha.getform.Response.Repository.ResponseRepository;
import com.skushwaha.getform.Forms.Entity.Form;
import com.skushwaha.getform.Forms.Repository.FormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ResponseServiceImpl implements ResponseService {

    private final ResponseRepository responseRepository;
    private final FormRepository formRepository;
    private final QuestionService questionService;

    @Override
    public ResponseDto submitResponse(
            UUID formId,
            CreateResponseRequest request
    ) {

        Form form = formRepository.findById(formId)
                .orElseThrow(() ->
                        new ClassCastException("Form not found"));

        // Form must be published
        if (!"PUBLISHED".equals(form.getStatus().name())) {
            throw new ClassCastException(
                    "Response cannot be submitted. Form is not published"
            );
        }

        // Check response limit
        if (form.getResponseLimit() != null) {

            long totalResponses =
                    responseRepository.countByFormId(formId);

            if (totalResponses >= form.getResponseLimit()) {
                throw new ClassCastException(
                        "Response limit reached"
                );
            }
        }

        // Prevent duplicate submission
        if (request.getRespondentEmail() != null &&
                responseRepository.existsByFormIdAndRespondentEmail(
                        formId,
                        request.getRespondentEmail()
                )) {

            throw new UserAlreadyExistsException(
                    "You have already submitted this form"
            );
        }

        List<String>quelist=questionService.getQuestionsByForm(formId).stream().map(QuestionResponse::getQuestion).toList();
        Map<String,Object>finalMap=new HashMap<>();

        for (String i:quelist){
            finalMap.put(i, request.getAnswers().getOrDefault(i, null));
        }
        Response response = Response.builder()
                .formId(formId)
                .respondentEmail(request.getRespondentEmail())
                .answers(finalMap)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        return mapToDto(
                responseRepository.save(response)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDto getResponse(
            UUID formId,
            Long responseId
    ) {

        Response response = getResponseFromForm(
                formId,
                responseId
        );

        return mapToDto(response);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseDto> getFormResponses(UUID formId) {

        if (!formRepository.existsById(formId)) {
            throw new ResourceNotFoundException("Form not found");
        }

        return responseRepository
                .findByFormIdOrderBySubmittedAtDesc(formId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseDto> getPendingPayments(UUID formId) {

        if (!formRepository.existsById(formId)) {
            throw new ResourceNotFoundException("Form not found");
        }

        return responseRepository
                .findByFormIdAndPaymentStatus(
                        formId,
                        PaymentStatus.PENDING
                )
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseDto> getSuccessfulPayments(UUID formId) {

        if (!formRepository.existsById(formId)) {
            throw new ResourceNotFoundException("Form not found");
        }

        return responseRepository
                .findByFormIdAndPaymentStatus(
                        formId,
                        PaymentStatus.SUCCESS
                )
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public ResponseDto updatePaymentStatus(
            UUID formId,
            Long responseId,
            String paymentStatus
    ) {

        Response response = getResponseFromForm(
                formId,
                responseId
        );

        PaymentStatus status;

        try {
            status = PaymentStatus.valueOf(
                    paymentStatus.toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new ClassCastException(
                    "Invalid payment status"
            );
        }

        response.setPaymentStatus(status);

        return mapToDto(
                responseRepository.save(response)
        );
    }

    @Override
    public void deleteResponse(
            UUID formId,
            Long responseId
    ) {

        Response response = getResponseFromForm(
                formId,
                responseId
        );

        responseRepository.delete(response);
    }

    private Response getResponseFromForm(
            UUID formId,
            Long responseId
    ) {

        return responseRepository
                .findByIdAndFormId(responseId, formId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Response not found"
                        ));
    }

    private ResponseDto mapToDto(Response response) {

        return ResponseDto.builder()
                .id(response.getId())
                .formId(response.getFormId())
                .respondentEmail(response.getRespondentEmail())
                .answers(response.getAnswers())
                .submittedAt(response.getSubmittedAt())
                .build();
    }
}
