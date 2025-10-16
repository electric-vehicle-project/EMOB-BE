/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Delivery;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {}
