<<<<<<< HEAD
package com.example.emob.entity;

import jakarta.persistence.*;
=======
/* EMOB-2025 */
package com.example.emob.entity;

import jakarta.persistence.*;
import java.util.Set;
import java.util.UUID;
>>>>>>> f514e41d121209766b1808e639b623d8b269ae3d
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

<<<<<<< HEAD
import java.util.Set;
import java.util.UUID;

=======
>>>>>>> f514e41d121209766b1808e639b623d8b269ae3d
@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Inventory {
<<<<<<< HEAD
    @Id
    @UuidGenerator
    UUID id;
    int totalQuantity;
    boolean isCompany = false;

    @OneToOne(mappedBy = "inventory",cascade = CascadeType.ALL,orphanRemoval = true)
    Dealer dealer;

    @OneToMany(mappedBy = "inventory",cascade = CascadeType.ALL,orphanRemoval = true)
    Set<InventoryItem> inventoryItems;
=======
    @Id @UuidGenerator UUID id;
    int quantity;
    boolean isCompany = false;

    @OneToOne(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    Dealer dealer;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<VehicleUnit> vehicleUnits;
>>>>>>> f514e41d121209766b1808e639b623d8b269ae3d
}
