/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.CustomerStatus;
import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.MemberShipLevel;
import com.example.emob.entity.Customer;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.CustomerMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.CustomerRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.CustomerResponse;
import com.example.emob.model.response.PageResponse;
import com.example.emob.repository.CustomerRepository;
import com.example.emob.service.impl.ICustomer;
import com.example.emob.util.AccountUtil;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class CustomerService implements ICustomer {

  @Autowired CustomerRepository customerRepository;

  @Autowired CustomerMapper customerMapper;

  @Autowired PageMapper pageMapper;

  @Autowired DealerPointRuleService dealerPointRuleService;

  @Override
  public APIResponse<CustomerResponse> create(CustomerRequest request) {
    try {
      Customer customer = customerMapper.toCustomer(request);
      customer.setStatus(CustomerStatus.ACTIVE);
      customer.setDealer(AccountUtil.getCurrentUser().getDealer());
      customer.setMemberShipLevel(MemberShipLevel.NORMAL);
      customerRepository.save(customer);
      CustomerResponse response = customerMapper.toCustomerResponse(customer);
      return APIResponse.success(response, "Created successfully");
    } catch (Exception e) {
      String errorMessage = e.getMessage().toLowerCase();
      if (errorMessage.contains("email")) {
        throw new GlobalException(ErrorCode.EMAIL_EXISTED);
      } else if (errorMessage.contains("phone")) {
        throw new GlobalException(ErrorCode.PHONE_EXISTED);
      } else {
        throw new GlobalException(ErrorCode.OTHER);
      }
    }
  }

  @Override
  public APIResponse<CustomerResponse> update(UUID id, CustomerRequest request) {
    try {
      Customer customer =
          customerRepository
              .findById(id)
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

      Customer newCustomer = customerMapper.updateCustomer(request, customer);
      customerRepository.save(newCustomer);

      return APIResponse.success(
          customerMapper.toCustomerResponse(customer), "Updated successfully");
    } catch (Exception e) {
      String errorMessage = e.getMessage().toLowerCase();
      if (errorMessage.contains("email")) {
        throw new GlobalException(ErrorCode.EMAIL_EXISTED);
      } else if (errorMessage.contains("phone")) {
        throw new GlobalException(ErrorCode.PHONE_EXISTED);
      } else {
        throw new GlobalException(ErrorCode.OTHER);
      }
    }
  }

  @Override
  public APIResponse<CustomerResponse> delete(UUID id) {
    try {
      Customer customer =
          customerRepository
              .findById(id)
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
      customer.setStatus(CustomerStatus.DELETED);
      customerRepository.save(customer);
      CustomerResponse response = customerMapper.toCustomerResponse(customer);
      return APIResponse.success(response, "Customer deleted successfully");
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF','MANAGER')")
  public APIResponse<CustomerResponse> get(UUID id) {
    try {
      Customer customer =
          customerRepository
              .findById(id)
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

      return APIResponse.success(customerMapper.toCustomerResponse(customer));
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  @PreAuthorize("hasAnyRole('DEALER_STAFF','MANAGER')")
  public APIResponse<PageResponse<CustomerResponse>> getAll(Pageable pageable) {
    try {
      Page<Customer> page =
          customerRepository.findAllByDealer(AccountUtil.getCurrentUser().getDealer(), pageable);
      PageResponse<CustomerResponse> response =
          pageMapper.toPageResponse(page, customerMapper::toCustomerResponse);
      return APIResponse.success(response);
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }
}
