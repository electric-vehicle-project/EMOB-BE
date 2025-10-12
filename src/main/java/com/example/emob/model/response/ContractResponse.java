package com.example.emob.model.response;

import com.example.emob.constant.ContractStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractResponse {
    UUID orderId;
    UUID contractId;
    String contractNumber;
    LocalDateTime createAt;
    ContractStatus status;
    LocalDateTime signDate;
}
