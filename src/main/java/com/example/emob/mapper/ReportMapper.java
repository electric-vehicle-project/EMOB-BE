package com.example.emob.mapper;

import com.example.emob.entity.Report;
import com.example.emob.model.response.ReportResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    ReportResponse toReportResponse(Report request);
}
