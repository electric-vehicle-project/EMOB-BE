package com.example.emob.mapper;

import com.example.emob.entity.InstallmentPlan;
import com.example.emob.model.request.installment.InstallmentRequest;
import com.example.emob.model.response.InstallmentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstallmentPlanMapper {
    InstallmentResponse toInstallmentResponse (InstallmentPlan installmentPlan);

    InstallmentPlan toInstallmentPlan (InstallmentRequest request);
}
