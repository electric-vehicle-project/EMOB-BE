package com.example.emob.mapper;

import com.example.emob.entity.CustomerFeedback;
import com.example.emob.entity.Report;
import com.example.emob.model.request.CustomerFeedbackRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerFeedbackMapper {
//    CustomerFeedback toCustomerFeedback (CustomerFeedbackRequest feedbackData);

    Report toReport (CustomerFeedback feedback);
}
