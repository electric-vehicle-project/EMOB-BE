/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.TestStatus;
import com.example.emob.model.request.TestDriveRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.TestDriveResponse;
import com.example.emob.model.response.VehicleUnitResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ITestDrive {

  APIResponse<TestDriveResponse> createSchedule(TestDriveRequest request);

  APIResponse<TestDriveResponse> updateSchedule(TestDriveRequest request, UUID id);

  APIResponse<TestDriveResponse> viewSchedule(UUID id);

  APIResponse<TestDriveResponse> cancelSchedule(UUID id);

  APIResponse<PageResponse<TestDriveResponse>> viewAllSchedules(
      Pageable pageable, String keyword, List<TestStatus> status);

  APIResponse<PageResponse<TestDriveResponse>> viewAllSchedulesByStaff(
      Pageable pageable, String keyword, List<TestStatus> status);

  APIResponse<List<VehicleUnitResponse>> getFreeVehiclesByDate(
      LocalDateTime scheduledAt, int duration, String model);
}
