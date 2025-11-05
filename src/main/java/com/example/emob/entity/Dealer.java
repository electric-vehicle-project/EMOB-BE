/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.Region;
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
public class Dealer {
  @Id @UuidGenerator UUID id;

  String name;

  @Column(unique = true)
  String emailContact;

  @Column(unique = true)
  String phoneContact;

  String country;

  @Enumerated(EnumType.STRING)
  Region region;

  String address;
  LocalDateTime createdAt;
  boolean isDeleted;

  @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<Account> accounts = new HashSet<>();

  @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<Report> reports = new HashSet<>();

  @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<TestDrive> testDrives = new HashSet<>();

  @ManyToMany(mappedBy = "dealers", cascade = CascadeType.ALL)
  Set<Promotion> promotions = new HashSet<>();

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "inventory_id")
  Inventory inventory;

  @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<Quotation> quotations = new HashSet<>();

  @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<DealerDiscountPolicy> discountPolicies = new HashSet<>();

  @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<Customer> customers = new HashSet<>();

  @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<VehicleRequest> vehicleRequests = new HashSet<>();
}
