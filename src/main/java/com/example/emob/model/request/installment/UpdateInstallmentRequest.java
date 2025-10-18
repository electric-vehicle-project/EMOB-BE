/* EMOB-2025 */
package com.example.emob.model.request.installment;

import com.example.emob.constant.InstallmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateInstallmentRequest {
  @NotNull(message = "Installment status must not be null")
  InstallmentStatus status;
}
