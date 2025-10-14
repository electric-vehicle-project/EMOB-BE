package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.VehicleStatus;
import com.example.emob.entity.VehiclePriceRule;
import com.example.emob.exception.GlobalException;
import com.example.emob.model.response.APIResponse;
import com.example.emob.repository.VehiclePriceRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehiclePriceRuleService {
    @Autowired
    VehiclePriceRuleRepository vehiclePriceRuleRepository;
    public void saveRule(VehicleStatus type, Double multiplier, String note) {
        VehiclePriceRule rule = VehiclePriceRule.builder()
                .vehicleStatus(type.toString())
                .multiplier(multiplier)
                .note(note)
                .build();
        vehiclePriceRuleRepository.save(rule);
    }

    public VehiclePriceRule getRule(VehicleStatus status) {
        return vehiclePriceRuleRepository.findById(status.toString()).orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    }
    public APIResponse<List<VehiclePriceRule>> getAllRules() {
        List<VehiclePriceRule> rules = new ArrayList<>();
        vehiclePriceRuleRepository.findAll().forEach(rules::add);
        return APIResponse.success(rules);
    }




}
