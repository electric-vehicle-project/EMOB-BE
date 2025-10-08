/* EMOB-2025 */
package com.example.emob.entity;

import com.example.emob.constant.InventoryStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
public class InventoryItem {
    @Id @UuidGenerator UUID id;

    int quantity;

    @Enumerated(EnumType.STRING)
    InventoryStatus status;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    @JsonIgnore
    Inventory inventory;

    @OneToOne
    @JoinColumn(name = "vehicle_id")
    @JsonIgnore
    ElectricVehicle vehicle;
}
