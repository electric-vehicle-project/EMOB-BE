/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.PromotionScope;
import com.example.emob.constant.PromotionStatus;
import com.example.emob.constant.PromotionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "promotion")
public class Promotion {
  @Id
  @GeneratedValue
  @Column(columnDefinition = "BINARY(16)", unique = true)
  UUID id;

  @Column(unique = true)
  String name; // tên chương trình

  String description; // chi tiết tên chương trình
  float value; // giá trị khuyến mãi
  float minValue; // giá trị khuyến mãi tối thiểu
  LocalDateTime startDate;
  LocalDateTime endDate;

  @Enumerated(EnumType.STRING)
  PromotionScope scope;

  @Enumerated(EnumType.STRING)
  PromotionType type;

  @Enumerated(EnumType.STRING)
  PromotionStatus status;

  LocalDateTime createAt;
  LocalDateTime updateAt;

  @ManyToOne
  @JoinColumn(name = "created_by", referencedColumnName = "id")
  Account createBy;

  @ManyToMany
  @JoinTable(
      name = "promotion_dealer",
      joinColumns = @JoinColumn(name = "promotion_id"),
      inverseJoinColumns = @JoinColumn(name = "dealer_id"))
  Set<Dealer> dealers = new HashSet<>();

  @ManyToMany
  @JoinTable(
      name = "promotion_vehicle",
      joinColumns = @JoinColumn(name = "promotion_id"),
      inverseJoinColumns = @JoinColumn(name = "vehicle_id"))
  Set<ElectricVehicle> vehicles = new HashSet<>();

  @OneToMany(mappedBy = "promotion")
  Set<QuotationItem> quotationItems = new HashSet<>();

  @OneToMany(mappedBy = "promotion")
  Set<SaleOrderItem> saleOrderItems = new HashSet<>();
}
