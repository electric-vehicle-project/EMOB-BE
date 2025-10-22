/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.VehicleRequestStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleRequest {
  @Id @UuidGenerator UUID id;
  boolean isDeleted = false;
  BigDecimal totalPrice;
  int totalQuantity;

  @Enumerated(EnumType.STRING)
  VehicleRequestStatus status;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "sale_order_id")
  SaleOrder saleOrder;

  @ManyToOne
  @JoinColumn(name = "dealer_id")
  Dealer dealer;

  @OneToMany(mappedBy = "vehicleRequest", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<VehicleRequestItem> vehicleRequestItems = new HashSet<>();
}
