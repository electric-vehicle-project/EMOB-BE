/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.ElectricVehicle;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectricVehicleRepository extends JpaRepository<ElectricVehicle, UUID> {
  Page<ElectricVehicle> findAllByIsDeletedFalse(Pageable pageable);
}
