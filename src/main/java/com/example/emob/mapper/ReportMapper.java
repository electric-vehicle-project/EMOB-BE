package com.example.emob.mapper;

import com.example.emob.entity.Report;
import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.response.ReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    @Mapping(source = "reportBy.id", target = "customerId")
    @Mapping(source = "reportBy.fullName", target = "fullName")
    ReportResponse toReportResponse(Report request);

    @Mapping(target = "reportBy", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "description", ignore = true)
    Report toReport(CreateReportRequest request);
}
