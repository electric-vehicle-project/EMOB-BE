package com.example.emob.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Inventory {
    @Id
    @UuidGenerator
    UUID id;
    int totalQuantity;
    boolean isCompany = false;

    @OneToOne(mappedBy = "inventory",cascade = CascadeType.ALL,orphanRemoval = true)
    Dealer dealer;

    @OneToMany(mappedBy = "inventory",cascade = CascadeType.ALL,orphanRemoval = true)
    Set<InventoryItem> inventoryItems;
}
