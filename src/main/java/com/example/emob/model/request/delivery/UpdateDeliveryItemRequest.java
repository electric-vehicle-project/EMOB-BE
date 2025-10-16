package com.example.emob.model.request.delivery;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateDeliveryItemRequest {
    @NotNull(message = "Vehicle ID cannot be null")
    UUID vehicleId;
    @NotBlank(message = "Remarks cannot be blank")
    String remarks;
}
