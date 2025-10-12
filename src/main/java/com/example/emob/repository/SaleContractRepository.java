package com.example.emob.repository;

import com.example.emob.entity.SaleContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SaleContractRepository extends JpaRepository<SaleContract, UUID> {
}
