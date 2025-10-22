/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.constant.QuotationStatus;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.Quotation;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, UUID> {
  Page<Quotation> findAllByIsDeletedFalseAndDealer(Dealer dealer, Pageable pageable);

  List<Quotation> findAllByIsDeletedFalseAndStatus(QuotationStatus status);
}
