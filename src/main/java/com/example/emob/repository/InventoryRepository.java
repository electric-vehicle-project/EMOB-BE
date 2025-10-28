/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Inventory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
  Inventory findInventoryByIsCompanyTrue();

  long countByIsCompanyTrue();

  // Nếu cần lọc theo mẫu xe cụ thể

}
