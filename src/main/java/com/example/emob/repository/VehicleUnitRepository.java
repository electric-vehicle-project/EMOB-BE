/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.VehicleStatus;
import com.example.emob.entity.ElectricVehicle;
import com.example.emob.entity.Inventory;
import com.example.emob.entity.VehicleUnit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleUnitRepository extends JpaRepository<VehicleUnit, UUID> {
  long countVehicleUnitByVehicle(ElectricVehicle vehicle);

  Optional<VehicleUnit> findByIdAndInventory(UUID id, Inventory inventory);

  Page<VehicleUnit> findAllByInventory(Inventory inventory, Pageable pageable);

  Page<VehicleUnit> findAllByVehicleAndInventory(
      ElectricVehicle vehicle, Inventory inventory, Pageable pageable);

  VehicleUnit findFirstByInventoryAndVehicleAndStatus(
      Inventory inventory, ElectricVehicle vehicle, VehicleStatus status);


  @Query("""
    SELECT v
    FROM VehicleUnit v
    WHERE v.inventory = :inventory
      AND (
        :keyword IS NULL 
        OR LOWER(v.vinNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(v.color) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
      AND (
        :statuses IS NULL 
        OR v.status IN :statuses
      )
""")
  Page<VehicleUnit> searchAndFilter(
          @Param("inventory") Inventory inventory,
          @Param("keyword") String keyword,
          @Param("statuses") List<VehicleStatus> statuses,
          Pageable pageable);


  @Query("""
    SELECT vu FROM VehicleUnit vu
    WHERE vu.inventory = :inventory
      AND vu.vehicle = :vehicle
      AND LOWER(vu.color) = LOWER(:color)
      AND vu.status = :status
      AND vu.inventory IS NOT NULL
    ORDER BY vu.productionYear ASC
""")
  List<VehicleUnit> findTopNByInventoryAndVehicleAndColorIgnoreCaseAndStatus(
          @Param("inventory") Inventory inventory,
          @Param("vehicle") ElectricVehicle vehicle,
          @Param("color") String color,
          @Param("status") VehicleStatus status,
          Pageable pageable
  );

}
