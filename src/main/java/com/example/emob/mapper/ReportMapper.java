/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.Report;
import com.example.emob.model.request.report.CreateReportRequest;
import com.example.emob.model.request.report.UpdateReportRequest;
import com.example.emob.model.response.ReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReportMapper {
  @Mapping(target = "reportId", source = "id")
  @Mapping(target = "fullName", source = "createBy.fullName")
  @Mapping(target = "customerId", source = "reportBy.id")
  @Mapping(target = "vehicleUnitId", source = "vehicleUnit.id")
  ReportResponse toReportResponse(Report request);

  Report toReport(CreateReportRequest request);

  @Mapping(target = "id", ignore = true) // không cho update id
  void updateReportFromRequest(UpdateReportRequest request, @MappingTarget Report report);
}
