package com.example.emob.model.request.delivery;

import com.example.emob.constant.DeliveryItemStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryItemRequest {
    @NotNull(message = "Vehicle ID cannot be null")
    UUID vehicleId;
    @NotBlank(message = "Remarks cannot be blank")
    String remarks;
    @NotNull(message = "Delivery item status cannot be null")
    DeliveryItemStatus status;
}
