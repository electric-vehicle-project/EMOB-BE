/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.TestStatus;
import com.example.emob.model.request.schedule.TestDriveRequest;
import com.example.emob.model.request.schedule.UpdateTestDriveRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.TestDriveResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ITestDrive {
  APIResponse<TestDriveResponse> createSchedule(TestDriveRequest request);

  APIResponse<TestDriveResponse> viewSchedule(UUID id);

  APIResponse<TestDriveResponse> updateSchedule(UpdateTestDriveRequest request, UUID id);

  APIResponse<TestDriveResponse> cancelSchedule(UUID id);

  APIResponse<PageResponse<TestDriveResponse>> viewAllSchedules(Pageable pageable, String keyword, TestStatus status);

  APIResponse<PageResponse<TestDriveResponse>> viewScheduleOfDealerStaff(Pageable pageable);
}
