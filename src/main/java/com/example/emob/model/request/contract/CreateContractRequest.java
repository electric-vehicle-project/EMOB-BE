package com.example.emob.model.request.contract;

import com.example.emob.entity.Dealer;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateContractRequest {
    @NotNull
    UUID orderId;
    @NotNull

    LocalDateTime createAt;
}
