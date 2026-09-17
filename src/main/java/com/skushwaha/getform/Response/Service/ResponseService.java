package com.skushwaha.getform.Response.Service;

import com.skushwaha.getform.Response.DTO.CreateResponseRequest;
import com.skushwaha.getform.Response.DTO.ResponseDto;

import java.util.List;
import java.util.UUID;

public interface ResponseService {

    ResponseDto submitResponse(UUID formId, CreateResponseRequest request);

    ResponseDto getResponse(UUID formId, Long responseId);

    List<ResponseDto> getFormResponses(UUID formId);

    List<ResponseDto> getPendingPayments(UUID formId);

    List<ResponseDto> getSuccessfulPayments(UUID formId);

    ResponseDto updatePaymentStatus(
            UUID formId,
            Long responseId,
            String paymentStatus
    );

    void deleteResponse(UUID formId, Long responseId);
}
