/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.ElectricVehicle;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectricVehicleRepository extends JpaRepository<ElectricVehicle, UUID> {}
