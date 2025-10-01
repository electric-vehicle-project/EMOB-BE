package com.example.emob.service;

import com.example.emob.constant.AccountStatus;
import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.TestStatus;
import com.example.emob.entity.Account;
import com.example.emob.entity.Customer;
import com.example.emob.entity.TestDrive;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.PageMapper;
import com.example.emob.mapper.TestDriveMapper;
import com.example.emob.model.request.schedule.TestDriveRequest;
import com.example.emob.model.request.schedule.UpdateTestDriveRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.model.response.TestDriveResponse;
import com.example.emob.repository.AccountRepository;
import com.example.emob.repository.CustomerRepository;
import com.example.emob.repository.TestDriveRepository;
import com.example.emob.service.iml.ITestDrive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TestDriveService implements ITestDrive {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TestDriveRepository testDriveRepository;


    @Autowired
    private TestDriveMapper testDriveMapper;

    @Autowired
    private PageMapper pageMapper;

    @Override
    public APIResponse<TestDriveResponse> createSchedule(TestDriveRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        Account salePerson = accountRepository.findById(request.getAccountId())
                            .filter(account -> AccountStatus.ACTIVE.equals(account.getStatus()))
                            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            TestDrive testDrive = testDriveMapper.toTestDrive(request);
            testDrive.setCustomer(customer);
            testDrive.setSalesperson(salePerson);
            testDrive.setStatus(TestStatus.PENDING);
            testDriveRepository.save(testDrive);
            TestDriveResponse testDriveResponse = testDriveMapper.toTestDriveResponse(testDrive);
            return APIResponse.success(testDriveResponse, "Create schedule successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }

    @Override
    public APIResponse<TestDriveResponse> viewSchedule(UUID id) {
        TestDrive testDrive = testDriveRepository.findById(id)
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        TestDriveResponse testDriveResponse = testDriveMapper.toTestDriveResponse(testDrive);
        return APIResponse.success(testDriveResponse, "View Schedule Test Drive Successfully");
    }

    @Override
    public APIResponse<TestDriveResponse> updateSchedule(UpdateTestDriveRequest request, UUID id) {
        TestDrive testDrive = testDriveRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            testDriveMapper.updateScheduleFromRequest(request, testDrive);
            testDriveRepository.save(testDrive);
            TestDriveResponse testDriveResponse = testDriveMapper.toTestDriveResponse(testDrive);
            return APIResponse.success(testDriveResponse, "Create schedule successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }

    @Override
    public APIResponse<TestDriveResponse> cancelSchedule(UUID id) {
        TestDrive testDrive = testDriveRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
        try {
            testDrive.setStatus(TestStatus.CANCELLED);
            testDriveRepository.save(testDrive);
            TestDriveResponse testDriveResponse = testDriveMapper.toTestDriveResponse(testDrive);
            return APIResponse.success(testDriveResponse, "Create schedule successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new GlobalException(ErrorCode.DATA_INVALID);
        } catch (DataAccessException ex) {
            throw new GlobalException(ErrorCode.DB_ERROR);
        } catch (Exception ex) {
            throw new GlobalException(ErrorCode.OTHER);
        }
    }

    @Override
    public APIResponse<PageResponse<TestDriveResponse>> viewAllSchedules(Pageable pageable) {
        Page<TestDrive> testDrives = testDriveRepository.findAll(pageable);
        PageResponse<TestDriveResponse> testDriveResponsePageResponse =
                    pageMapper.toPageResponse(testDrives, testDriveMapper::toTestDriveResponse);
        return APIResponse.success(testDriveResponsePageResponse, "View All Schedules Successfully");
    }
}
