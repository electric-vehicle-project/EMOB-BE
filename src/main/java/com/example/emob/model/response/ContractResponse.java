/* EMOB-2025 */
package com.example.emob.model.response;

import com.example.emob.constant.ContractStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
