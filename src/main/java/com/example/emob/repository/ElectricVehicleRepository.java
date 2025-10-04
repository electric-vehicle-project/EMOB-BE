package com.example.emob.repository;

import com.example.emob.entity.ElectricVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ElectricVehicleRepository extends JpaRepository<ElectricVehicle, UUID> {

}
