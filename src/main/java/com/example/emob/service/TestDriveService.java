/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.*;
import com.example.emob.entity.*;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.ElectricVehicleMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.mapper.TestDriveMapper;
import com.example.emob.model.request.TestDriveRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.TestDriveResponse;
import com.example.emob.model.response.VehicleUnitResponse;
import com.example.emob.repository.*;
import com.example.emob.service.impl.ITestDrive;
import com.example.emob.util.AccountUtil;
import com.example.emob.util.NotificationHelper;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class TestDriveService implements ITestDrive {

  @Autowired private CustomerRepository customerRepository;
  @Autowired private AccountRepository accountRepository;
  @Autowired private TestDriveRepository testDriveRepository;
  @Autowired private VehicleUnitRepository vehicleUnitRepository;
  @Autowired private TestDriveMapper testDriveMapper;
  @Autowired private PageMapper pageMapper;
  @Autowired private EmailService emailService;
  @Autowired private ElectricVehicleMapper vehicleMapper;

  @Override
  @PreAuthorize("hasRole('DEALER_STAFF')")
  public APIResponse<TestDriveResponse> createSchedule(TestDriveRequest request) {
    LocalDateTime scheduledAt = request.getScheduledAt();
    LocalTime time = scheduledAt.toLocalTime();

    if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(17, 30))) {
      throw new GlobalException(ErrorCode.INVALID_DATE, "Outside of working hours (8:00–17:30)");
    }

    Customer customer =
        customerRepository
            .findById(request.getCustomerId())
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Customer not found"));

    Account salePerson = AccountUtil.getCurrentUser();

    VehicleUnit vehicle =
        vehicleUnitRepository
            .findById(request.getTestDriveVehicleId())
            .orElseThrow(
                () -> new GlobalException(ErrorCode.NOT_FOUND, "Electric vehicle not found"));

    if (!VehicleStatus.TEST_DRIVE.equals(vehicle.getStatus())) {
      throw new GlobalException(
          ErrorCode.DATA_INVALID, "This vehicle is not available for test drive");
    }

    // Check staff & vehicle availability
    boolean isFree = checkFreeTime(scheduledAt, salePerson, request.getDuration(), vehicle);
    if (!isFree) {
      throw new GlobalException(ErrorCode.STAFF_BUSY, "Staff or vehicle is busy at this time");
    }

    try {
      TestDrive testDrive = testDriveMapper.toTestDrive(request);
      testDrive.setCustomer(customer);
      testDrive.setSalesperson(salePerson);
      testDrive.setStatus(TestStatus.PENDING);
      testDrive.setCreatedAt(LocalDateTime.now());
      testDrive.setVehicleUnit(vehicle);
      testDrive.setDealer(salePerson.getDealer());

      testDriveRepository.save(testDrive);

      emailService.sendEmail(
          "Xác nhận lịch lái thử",
          "Đặt lịch thành công",
          "Chúng tôi cảm ơn bạn đã đăng ký lịch lái thử.",
          NotificationHelper.CONFIRM_TEST_DRIVE,
          "Lịch hẹn của bạn đã được xác nhận thành công.",
          "https://app.diagrams.net/#G1m4SbslLmKuduNeCj6kPdxNauEpzCLl4J#%7B%22pageId%22%3A%22rHYpNvzPq7mJ_Pk65GUX%22%7D",
          "Cảm ơn quý khách",
          "Quý khách vui lòng đến đúng giờ hẹn.",
          customer.getFullName(),
          "Xác nhận",
          customer.getEmail());

      TestDriveResponse response = testDriveMapper.toTestDriveResponse(testDrive);
      return APIResponse.success(response, "Create schedule successfully");
    } catch (DataIntegrityViolationException ex) {
      throw new GlobalException(ErrorCode.DATA_INVALID);
    } catch (DataAccessException ex) {
      throw new GlobalException(ErrorCode.DB_ERROR);
    }
  }

  @Override
  @PreAuthorize("hasRole('DEALER_STAFF')")
  public APIResponse<TestDriveResponse> updateSchedule(TestDriveRequest request, UUID id) {
    // khai bao
    Customer customer = null;
    // tìm xe
    TestDrive testDrive =
        testDriveRepository
            .findById(id)
            .orElseThrow(
                () -> new GlobalException(ErrorCode.NOT_FOUND, "Test drive schedule not found"));
    // cập nhật
    LocalDateTime scheduledAt = request.getScheduledAt();
    LocalTime time = scheduledAt.toLocalTime();

    if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(17, 30))) {
      throw new GlobalException(ErrorCode.INVALID_DATE, "Outside of working hours (8:00–17:30)");
    }
    Account salePerson = AccountUtil.getCurrentUser();
    if (request.getCustomerId() != null) {
      customer =
          customerRepository
              .findById(request.getCustomerId())
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Customer not found"));
    }
    VehicleUnit vehicle =
        vehicleUnitRepository
            .findById(request.getTestDriveVehicleId())
            .orElseThrow(
                () -> new GlobalException(ErrorCode.NOT_FOUND, "Electric vehicle not found"));

    if (!VehicleStatus.TEST_DRIVE.equals(vehicle.getStatus())) {
      throw new GlobalException(
          ErrorCode.DATA_INVALID, "This vehicle is not available for test drive");
    }

    // Check staff & vehicle availability
    boolean isFree = checkFreeTime(scheduledAt, salePerson, request.getDuration(), vehicle);
    if (!isFree) {
      throw new GlobalException(ErrorCode.STAFF_BUSY, "Staff or vehicle is busy at this time");
    }

    try {
      testDrive.setScheduledAt(scheduledAt);
      testDrive.setDuration(request.getDuration());
      testDrive.setLocation(request.getLocation());
      testDrive.setVehicleUnit(vehicle);
      testDrive.setUpdateAt(LocalDateTime.now());
      if (customer != null) {
        testDrive.setCustomer(customer);
      }
      testDriveRepository.save(testDrive);
      TestDriveResponse response = testDriveMapper.toTestDriveResponse(testDrive);
      return APIResponse.success(response, "Update schedule successfully");
    } catch (DataIntegrityViolationException ex) {
      throw new GlobalException(ErrorCode.DATA_INVALID);
    } catch (DataAccessException ex) {
      throw new GlobalException(ErrorCode.DB_ERROR);
    } catch (Exception ex) {
      throw new GlobalException(ErrorCode.OTHER);
    }
  }

  @Override
  @PreAuthorize("hasAnyRole('MANAGER', 'DEALER_STAFF')")
  public APIResponse<TestDriveResponse> viewSchedule(UUID id) {
    TestDrive testDrive =
        testDriveRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    return APIResponse.success(
        testDriveMapper.toTestDriveResponse(testDrive), "View schedule successfully");
  }

  @Override
  @PreAuthorize("hasRole('DEALER_STAFF')")
  public APIResponse<TestDriveResponse> cancelSchedule(UUID id) {
    TestDrive testDrive =
        testDriveRepository
            .findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    try {
      testDrive.setStatus(TestStatus.CANCELLED);
      testDriveRepository.save(testDrive);
      return APIResponse.success(
          testDriveMapper.toTestDriveResponse(testDrive), "Cancel schedule successfully");
    } catch (DataIntegrityViolationException ex) {
      throw new GlobalException(ErrorCode.DATA_INVALID);
    } catch (DataAccessException ex) {
      throw new GlobalException(ErrorCode.DB_ERROR);
    } catch (Exception ex) {
      throw new GlobalException(ErrorCode.OTHER);
    }
  }

  @Override
  @PreAuthorize("hasAnyRole('MANAGER', 'DEALER_STAFF')")
  public APIResponse<PageResponse<TestDriveResponse>> viewAllSchedules(
      Pageable pageable, String keyword, List<TestStatus> status) {

    Page<TestDrive> testDrives =
        testDriveRepository.searchAndFilter(
            AccountUtil.getCurrentUser().getDealer(), keyword, status, pageable);
    PageResponse<TestDriveResponse> response =
        pageMapper.toPageResponse(testDrives, testDriveMapper::toTestDriveResponse);
    return APIResponse.success(response, "View all schedules successfully");
  }

  @Override
  @PreAuthorize("hasRole('DEALER_STAFF')")
  public APIResponse<PageResponse<TestDriveResponse>> viewAllSchedulesByStaff(
      Pageable pageable, String keyword, List<TestStatus> status) {

    Page<TestDrive> testDrives =
        testDriveRepository.searchAndFilterByStaff(
            AccountUtil.getCurrentUser().getDealer(),
            AccountUtil.getCurrentUser(),
            keyword,
            status,
            pageable);
    PageResponse<TestDriveResponse> response =
        pageMapper.toPageResponse(testDrives, testDriveMapper::toTestDriveResponse);
    return APIResponse.success(response, "View all schedules successfully");
  }

  @PreAuthorize("hasRole('DEALER_STAFF')")
  public APIResponse<List<VehicleUnitResponse>> getFreeVehiclesByDate(
      LocalDateTime scheduledAt, int duration, String model) {
    LocalDateTime startAt = scheduledAt.minusMinutes(30);
    LocalDateTime endAt = scheduledAt.plusMinutes(duration + 30);
    List<VehicleUnitResponse> responses = new ArrayList<>();
    List<VehicleUnit> vehicleUnits =
        vehicleUnitRepository.findAvailableVehiclesByTimeRangeAndModel(startAt, endAt, model);
    for (VehicleUnit vu : vehicleUnits) {
      responses.add(vehicleMapper.toVehicleUnitResponse(vu));
    }
    return APIResponse.success(responses, "Get free vehicles successfully");
  }

  private boolean checkFreeTime(
      LocalDateTime scheduledAt, Account staff, int duration, VehicleUnit vehicleUnit) {
    LocalDateTime endAt = scheduledAt.plusMinutes(duration);

    // 1️⃣ Kiểm tra staff
    Set<TestDrive> staffTestDrives = staff.getTestDrives();
    if (staffTestDrives != null && !staffTestDrives.isEmpty()) {
      for (TestDrive td : staffTestDrives) {
        if (td.getStatus() == TestStatus.CANCELLED) continue;
        LocalDateTime s = td.getScheduledAt();
        LocalDateTime e = s.plusMinutes(td.getDuration());
        if (!endAt.isBefore(s) && !scheduledAt.isAfter(e)) return false;
      }
    }

    // 2️⃣ Kiểm tra vehicle
    Set<TestDrive> vehicleTestDrives = vehicleUnit.getTestDrives(); // <== sửa tên cho đúng plural
    if (vehicleTestDrives != null && !vehicleTestDrives.isEmpty()) {
      for (TestDrive td : vehicleTestDrives) {
        if (td.getStatus() == TestStatus.CANCELLED) continue;
        LocalDateTime s = td.getScheduledAt();
        LocalDateTime e = s.plusMinutes(td.getDuration());
        if (!endAt.isBefore(s) && !scheduledAt.isAfter(e)) return false;
      }
    }

    return true;
  }
}
