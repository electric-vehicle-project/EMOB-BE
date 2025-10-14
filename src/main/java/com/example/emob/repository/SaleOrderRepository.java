package com.example.emob.repository;

import com.example.emob.entity.SaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SaleOrderRepository extends JpaRepository<SaleOrder, UUID> {
    List<SaleOrder> findAllByCustomerId (UUID customerId);
}
