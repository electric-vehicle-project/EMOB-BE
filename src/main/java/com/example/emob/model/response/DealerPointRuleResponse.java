package com.example.emob.model.response;

import java.math.BigDecimal;
import java.util.UUID;

public class DealerPointRuleResponse {
    UUID dealerId;
    String rank;
    BigDecimal discount_price;
    int point;
}
