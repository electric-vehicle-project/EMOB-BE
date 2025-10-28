/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.constant.TestStatus;
import com.example.emob.model.request.schedule.TestDriveRequest;
import com.example.emob.model.request.schedule.UpdateTestDriveRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.TestDriveResponse;
import com.example.emob.service.TestDriveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/test-drive")
@SecurityRequirement(name = "api")
@Tag(name = "Test Drive Controller", description = "Endpoints for managing schedule test drive")
public class TestDriveController {

  @Autowired TestDriveService testDriveService;

  @PostMapping
  @Operation(
      summary = "Create Schedule Test Drive",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Create Schedule Test Drive",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = TestDriveRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Account A",
                            value =
                                """
                                                                   {
                                                        "customerId": "29e59bdf-9dcd-11f0-ac59-0242ac110002",
                                                         "accountId": "a154190f-cdfc-4bfd-8d97-35719d608eea",
                                                         "location": "HCM",
                                                         "status": "PENDING",
                                                         "duration": 20,
                                                         "scheduledAt": "2025-10-01T04:46:39.236Z"
                                                    }


                                                    """)
                      })))
  public ResponseEntity<APIResponse<TestDriveResponse>> createSchedule(
      @RequestBody @Valid TestDriveRequest request) {
    return ResponseEntity.ok(testDriveService.createSchedule(request));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get Schedule Test Drive")
  public ResponseEntity<APIResponse<TestDriveResponse>> viewSchedule(@PathVariable UUID id) {
    return ResponseEntity.ok(testDriveService.viewSchedule(id));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update Schedule Test Drive")
  public ResponseEntity<APIResponse<TestDriveResponse>> updateSchedule(
      @RequestBody @Valid UpdateTestDriveRequest request, @PathVariable UUID id) {
    return ResponseEntity.ok(testDriveService.updateSchedule(request, id));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Cancel Schedule Test Drive")
  public ResponseEntity<APIResponse<TestDriveResponse>> cancelSchedule(@PathVariable UUID id) {
    return ResponseEntity.ok(testDriveService.cancelSchedule(id));
  }

  @GetMapping("/schedules")
  @Operation(summary = "View All Schedule Test Drive")
  public ResponseEntity<APIResponse<PageResponse<TestDriveResponse>>> viewAllSchedules(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) List<TestStatus> status,
      @RequestParam(defaultValue = "scheduledAt") String sortField,
      @RequestParam(defaultValue = "desc") String sortDir) {

    Sort sort = Sort.by(sortField);
    sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
    Pageable pageable = PageRequest.of(page, size, sort);

    return ResponseEntity.ok(testDriveService.viewAllSchedules(pageable, keyword, status));
  }
}
