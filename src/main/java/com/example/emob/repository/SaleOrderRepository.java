/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.SaleOrder;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleOrderRepository extends JpaRepository<SaleOrder, UUID> {}
