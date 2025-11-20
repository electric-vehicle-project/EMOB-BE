/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.VehicleStatus;
import com.example.emob.entity.ElectricVehicle;
import com.example.emob.entity.Inventory;
import com.example.emob.entity.VehicleUnit;
import java.time.LocalDateTime;
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

  @Query("""
    SELECT vu
    FROM VehicleUnit vu
    WHERE vu.vehicle = :vehicle
      AND vu.inventory = :inventory
      AND (:statuses IS NULL OR vu.status IN :statuses)
""")
  Page<VehicleUnit> findAllFiltered(
          @Param("vehicle") ElectricVehicle vehicle,
          @Param("inventory") Inventory inventory,
          @Param("statuses") List<VehicleStatus> statuses,
          Pageable pageable
  );

  VehicleUnit findFirstByInventoryAndVehicleAndStatus(
      Inventory inventory, ElectricVehicle vehicle, VehicleStatus status);

  @Query(
      """
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

  @Query(
      """
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
      Pageable pageable);

  /**
   * Lấy danh sách xe TEST_DRIVE của model chỉ định, chưa bị trùng lịch trong khoảng thời gian. -
   * Kiểm tra overlap: (scheduled_at < endAt) AND (scheduled_at + duration > startAt) - Sử dụng
   * DATE_ADD để cộng phút trong MySQL
   */
  @Query(
      value =
          """
        SELECT v.*
        FROM vehicle_unit v
        JOIN electric_vehicle ev ON v.vehicle_id = ev.id
        WHERE v.status = 'TEST_DRIVE'
          AND ev.model = :model
          AND v.id NOT IN (
              SELECT t.vehicle_unit
              FROM test_drive t
              WHERE t.status <> 'CANCELLED'
                AND (
                    t.scheduled_at < :endAt
                    AND DATE_ADD(t.scheduled_at, INTERVAL t.duration MINUTE) > :startAt
                )
                AND  v.inventory_id = :inventoryId
          )
    """,
      nativeQuery = true)
  List<VehicleUnit> findAvailableVehiclesByTimeRangeAndModel(
      @Param("startAt") LocalDateTime startAt,
      @Param("endAt") LocalDateTime endAt,
      @Param("model") String model,
      @Param("inventoryId") UUID inventoryId
  );

  @Query("""
    SELECT v
    FROM VehicleUnit v
    WHERE LOWER(v.vinNumber) = LOWER(:vin)
""")
  Optional<VehicleUnit> findByVinNumber(@Param("vin") String vin);

  @Query(
      """
    SELECT vu
    FROM VehicleUnit vu
    JOIN vu.inventory inv
    WHERE vu.color = :color
      AND vu.vehicle.model = :model
      AND inv.isCompany = true
""")
  List<VehicleUnit> findVehicleUnitInDealerInventory(
      @Param("color") String color, @Param("model") String model);
}
