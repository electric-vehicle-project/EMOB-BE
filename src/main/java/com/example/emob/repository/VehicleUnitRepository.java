/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.ElectricVehicle;
import com.example.emob.entity.Inventory;
import com.example.emob.entity.VehicleUnit;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleUnitRepository extends JpaRepository<VehicleUnit, UUID> {
  long countVehicleUnitByVehicle(ElectricVehicle vehicle);

  Optional<VehicleUnit> findByIdAndInventory(UUID id, Inventory inventory);

  Page<VehicleUnit> findAllByInventory(Inventory inventory, Pageable pageable);

  Page<VehicleUnit> findAllByVehicleAndInventory(
      ElectricVehicle vehicle, Inventory inventory, Pageable pageable);
}
