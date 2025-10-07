<<<<<<< HEAD
package com.example.emob.repository;

import com.example.emob.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findInventoryByIsCompanyTrue();
=======
/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Inventory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findInventoryByIsCompanyTrue();

>>>>>>> f514e41d121209766b1808e639b623d8b269ae3d
    long countByIsCompanyTrue();
}
