package com.example.emob.service.impl;

import com.example.emob.constant.MemberShipLevel;
import com.example.emob.entity.DealerPointRule;
import com.example.emob.model.response.APIResponse;

import java.math.BigDecimal;
import java.util.List;

public interface IDealerPointRule {
    APIResponse<String> saveRule (MemberShipLevel level, String dealerId, int minPoints, BigDecimal price);
    DealerPointRule getRule (MemberShipLevel level);
    APIResponse<List<DealerPointRule>> getAllRules();
}
