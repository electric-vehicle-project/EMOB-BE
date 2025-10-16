/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.DeliveryStatus;
import jakarta.persistence.*;
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
public class Delivery {
  @Id @UuidGenerator UUID id;

  LocalDateTime deliveryDate;

  int quantity;

  boolean isDeleted;

  @Enumerated(EnumType.STRING)
  DeliveryStatus status;

  LocalDateTime createAt;

  LocalDateTime updateAt;

  LocalDateTime confirmAt;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "saleContract_id")
  SaleContract saleContract;

  @OneToMany(mappedBy = "delivery", orphanRemoval = true, cascade = CascadeType.ALL)
  Set<DeliveryItem> deliveryItems = new HashSet<>();
}
