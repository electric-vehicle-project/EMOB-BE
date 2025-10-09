/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Promotion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, UUID> {}
