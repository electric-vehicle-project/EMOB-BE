/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Inventory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findInventoryByIsCompanyTrue();

    long countByIsCompanyTrue();
}