/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.VehicleStatus;
import com.example.emob.constant.VehicleType;
import com.example.emob.entity.ElectricVehicle;
import java.util.UUID;

import com.example.emob.entity.Inventory;
import com.example.emob.entity.VehicleUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ElectricVehicleRepository extends JpaRepository<ElectricVehicle, UUID> {
  Page<ElectricVehicle> findAllByIsDeletedFalse(Pageable pageable);

  @Query("""
SELECT e
FROM ElectricVehicle e
WHERE e.isDeleted = false
  AND (:keyword IS NULL OR LOWER(e.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(e.model) LIKE LOWER(CONCAT('%', :keyword, '%')))
  AND (:type IS NULL OR e.type = :type)
""")
  Page<ElectricVehicle> searchAndFilter(
          @Param("keyword") String keyword,
          @Param("type") VehicleType type,
          Pageable pageable);
}
