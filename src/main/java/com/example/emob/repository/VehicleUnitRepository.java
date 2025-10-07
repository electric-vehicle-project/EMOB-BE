
package com.example.emob.repository;

import com.example.emob.entity.ElectricVehicle;
import com.example.emob.entity.VehicleUnit;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleUnitRepository extends JpaRepository<VehicleUnit, UUID> {
    long countVehicleUnitByVehicle(ElectricVehicle vehicle);
}
