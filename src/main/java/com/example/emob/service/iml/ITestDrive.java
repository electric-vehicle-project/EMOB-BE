package com.example.emob.service.iml;

import com.example.emob.model.request.schedule.TestDriveRequest;
import com.example.emob.model.request.schedule.UpdateTestDriveRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.TestDriveResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ITestDrive {
    APIResponse<TestDriveResponse> createSchedule (TestDriveRequest request);

    APIResponse<TestDriveResponse> viewSchedule (UUID id);

    APIResponse<TestDriveResponse> updateSchedule (UpdateTestDriveRequest request, UUID id);

    APIResponse<TestDriveResponse> cancelSchedule (UUID id);

    APIResponse<PageResponse<TestDriveResponse>> viewAllSchedules (Pageable pageable);
}
