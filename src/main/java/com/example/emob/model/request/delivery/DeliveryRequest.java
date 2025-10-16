package com.example.emob.model.request.delivery;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryRequest {
    @NotNull(message = "Contract ID cannot be null")
    UUID contractId;
    @NotEmpty(message = "Delivery items cannot be empty")
    @Valid
    Set<DeliveryItemRequest> deliveryItems;
    @NotNull(message = "Delivery date cannot be null")
    LocalDateTime deliveryDate;
}
