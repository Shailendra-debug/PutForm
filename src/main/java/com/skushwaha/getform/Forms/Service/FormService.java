package com.skushwaha.getform.Forms.Service;

import com.skushwaha.getform.Forms.DTO.CreateFormRequest;
import com.skushwaha.getform.Forms.DTO.FormResponse;
import com.skushwaha.getform.Forms.DTO.UpdateFormRequest;

import java.util.List;
import java.util.UUID;

public interface FormService {

    FormResponse createForm(
            Long ownerId,
            CreateFormRequest request
    );

    FormResponse getForm(
            UUID formId);

    FormResponse getPublicForm(
            UUID formId
    );

    List<FormResponse> getMyForms(
            Long ownerId
    );

    FormResponse updateForm(
            UUID formId,
            Long ownerId,
            UpdateFormRequest request
    );

    FormResponse publishForm(
            UUID formId,
            Long ownerId
    );

    FormResponse closeForm(
            UUID formId,
            Long ownerId
    );

    FormResponse archiveForm(
            UUID formId,
            Long ownerId
    );

    void deleteForm(
            UUID formId,
            Long ownerId
    );
}
