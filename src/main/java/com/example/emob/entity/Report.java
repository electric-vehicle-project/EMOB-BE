/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.ReportStatus;
import com.example.emob.constant.ReportType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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
@Table(name = "Report")
public class Report {
  @Id @UuidGenerator UUID id;

  String title;
  String description;

  @Enumerated(EnumType.STRING)
  ReportType type;

  @ManyToOne
  @JoinColumn(name = "reportBy", referencedColumnName = "id")
  Customer reportBy;

  @Enumerated(EnumType.STRING)
  ReportStatus status;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;
  String solution;

  @ManyToOne
  @JoinColumn(name = "accountId", referencedColumnName = "id")
  Account createBy;

  @ManyToOne
  @JoinColumn(name = "dealer_id", referencedColumnName = "id")
  Dealer dealer;

  @ManyToOne
  @JoinColumn(name = "vehicleUnit_id")
  @JsonIgnore
  VehicleUnit vehicleUnit;
}
