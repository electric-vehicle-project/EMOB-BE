package com.example.emob.repository;

import com.example.emob.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findInventoryByIsCompanyTrue();
    long countByIsCompanyTrue();
}
