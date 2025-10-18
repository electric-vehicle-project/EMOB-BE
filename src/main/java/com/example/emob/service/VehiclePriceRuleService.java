/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.VehicleStatus;
import com.example.emob.entity.VehiclePriceRule;
import com.example.emob.exception.GlobalException;
import com.example.emob.model.request.VehiclePriceRuleRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.repository.VehiclePriceRuleRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class VehiclePriceRuleService {
  @Autowired VehiclePriceRuleRepository vehiclePriceRuleRepository;

  @PreAuthorize("hasRole('ADMIN')")
  public void saveRule(List<VehiclePriceRuleRequest> vehiclePriceRuleRequests) {
    for (VehiclePriceRuleRequest req : vehiclePriceRuleRequests) {
      String status = req.getVehicleStatus().toString();
      VehiclePriceRule rule =
          VehiclePriceRule.builder()
              .vehicleStatus(status)
              .multiplier(req.getMultiplier())
              .note(req.getNote())
              .build();
      vehiclePriceRuleRepository.save(rule);
    }
  }

  public VehiclePriceRule getRule(VehicleStatus status) {
    return vehiclePriceRuleRepository
        .findById(status.toString())
        .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Price rule not found"));
  }

  public APIResponse<List<VehiclePriceRule>> getAllRules() {
    List<VehiclePriceRule> rules = new ArrayList<>();
    vehiclePriceRuleRepository.findAll().forEach(rules::add);
    return APIResponse.success(rules);
  }
}
