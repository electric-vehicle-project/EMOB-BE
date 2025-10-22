/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.Dealer;
import com.example.emob.entity.SaleOrder;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleOrderRepository extends JpaRepository<SaleOrder, UUID> {
  Page<SaleOrder> findAllByDealerAndVehicleRequestIsNotNull(Dealer dealer, Pageable pageable);

  Page<SaleOrder> findAllByDealerAndQuotationIsNotNull(Dealer dealer, Pageable pageable);
}
