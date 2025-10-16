/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.SaleContract;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleContractRepository extends JpaRepository<SaleContract, UUID> {}
