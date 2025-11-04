/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.TestStatus;
import com.example.emob.entity.VehicleUnit;
import com.example.emob.mapper.ElectricVehicleMapper;
import com.example.emob.model.request.TestDriveRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.TestDriveResponse;
import com.example.emob.model.response.VehicleUnitResponse;
import com.example.emob.service.TestDriveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/test-drives")
@SecurityRequirement(name = "api")
@Tag(name = "🚗 Test Drive Controller", description = "Endpoints for scheduling and managing vehicle test drives")
public class TestDriveController {

  @Autowired
  private TestDriveService testDriveService;

  // 🔹 CREATE
  @Operation(
          summary = "Create a new Test Drive schedule",
          description = "Dealer staff can create a new test drive schedule for a customer.",
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  required = true,
                  description = "Test drive details",
                  content = @Content(
                          schema = @Schema(implementation = TestDriveRequest.class),
                          examples = @ExampleObject(
                                  name = "Example Request",
                                  value = """
                    {
                      "customerId": "e1b82df7-09b8-4b6b-b7cf-8a3ef705e412",
                      "testDriveVehicleId": "32a6f8fa-7e1a-4b7b-82d3-56f882871ff0",
                      "location": "EV Showroom District 9",
                      "duration": 60,
                      "scheduledAt": "2025-11-06T10:00:00"
                    }
                  """
                          )
                  )
          )
  )
  @PostMapping
  public ResponseEntity<APIResponse<TestDriveResponse>> createSchedule(
          @Valid @RequestBody TestDriveRequest request
  ) {
    return ResponseEntity.ok(testDriveService.createSchedule(request));
  }

  // 🔹 UPDATE
  @Operation(
          summary = "Update a Test Drive schedule",
          description = "Dealer staff can update a new test drive schedule for a customer.",
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  required = true,
                  description = "Test drive details",
                  content = @Content(
                          schema = @Schema(implementation = TestDriveRequest.class),
                          examples = @ExampleObject(
                                  name = "Example Request",
                                  value = """
                    {
                      "customerId": "e1b82df7-09b8-4b6b-b7cf-8a3ef705e412",
                      "testDriveVehicleId": "32a6f8fa-7e1a-4b7b-82d3-56f882871ff0",
                      "location": "EV Showroom District 9",
                      "duration": 60,
                      "scheduledAt": "2025-11-06T10:00:00"
                    }
                  """
                          )
                  )
          )
  )
  @PutMapping("/{id}")

  public ResponseEntity<APIResponse<TestDriveResponse>> updateSchedule(
          @Parameter(description = "ID of the test drive schedule") @PathVariable UUID id,
          @Valid @RequestBody TestDriveRequest request
  ) {
    return ResponseEntity.ok(testDriveService.updateSchedule(request, id));
  }

  // 🔹 VIEW DETAIL
  @Operation(summary = "View detail of a test drive schedule by ID")
  @GetMapping("/{id}")
  public ResponseEntity<APIResponse<TestDriveResponse>> viewSchedule(
          @Parameter(description = "ID of the test drive schedule") @PathVariable UUID id
  ) {
    return ResponseEntity.ok(testDriveService.viewSchedule(id));
  }

  // 🔹 CANCEL
  @Operation(summary = "Cancel a test drive schedule")
  @DeleteMapping("/{id}")
  public ResponseEntity<APIResponse<TestDriveResponse>> cancelSchedule(
          @Parameter(description = "ID of the test drive schedule") @PathVariable UUID id
  ) {
    return ResponseEntity.ok(testDriveService.cancelSchedule(id));
  }

  // 🔹 LIST ALL
  @Operation(
          summary = "View all test drive schedules (Dealer or Manager)",
          description = "Paginated list of all test drives filtered by keyword or status."
  )
  @GetMapping
  public ResponseEntity<APIResponse<PageResponse<TestDriveResponse>>> viewAllSchedules(
          @Parameter(description = "Page index (default 0)") @RequestParam(defaultValue = "0") int page,
          @Parameter(description = "Page size (default 10)") @RequestParam(defaultValue = "10") int size,
          @Parameter(description = "Keyword search by customer name or location") @RequestParam(required = false) String keyword,
          @Parameter(description = "Filter by test drive status") @RequestParam(required = false) List<TestStatus> status
  ) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(testDriveService.viewAllSchedules(pageable, keyword, status));
  }

  // 🔹 LIST STAFF
  @Operation(
          summary = "View all test drive schedules of current staff",
          description = "Used by dealer staff to view their own test drive appointments."
  )
  @GetMapping("/staff")
  public ResponseEntity<APIResponse<PageResponse<TestDriveResponse>>> viewAllSchedulesByStaff(
          @Parameter(description = "Page index (default 0)") @RequestParam(defaultValue = "0") int page,
          @Parameter(description = "Page size (default 10)") @RequestParam(defaultValue = "10") int size,
          @Parameter(description = "Keyword search by customer name or location") @RequestParam(required = false) String keyword,
          @Parameter(description = "Filter by test drive status") @RequestParam(required = false) List<TestStatus> status
  ) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(testDriveService.viewAllSchedulesByStaff(pageable, keyword, status));
  }

  // 🔹 FREE VEHICLES
  @Operation(
          summary = "Get available vehicles within a specific time range",
          description = "Returns list of vehicles not scheduled for any test drive during the selected time range (adds 30 min before and after for preparation)."
  )
  @GetMapping("/free-vehicles")
  public ResponseEntity<APIResponse<List<VehicleUnitResponse>>> getFreeVehiclesByDate(
          @Parameter(description = "Scheduled start time, format: yyyy-MM-dd'T'HH:mm:ss")
          @RequestParam("scheduledAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime scheduledAt,
          @Parameter(description = "Duration of the test drive in minutes (default 60)")
          @RequestParam("duration") int duration,
           @RequestParam("model") String model
  ) {
    return ResponseEntity.ok(testDriveService.getFreeVehiclesByDate(scheduledAt, duration,model));
  }
}
