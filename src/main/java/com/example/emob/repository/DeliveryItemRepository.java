/* EMOB-2025 */
package com.example.emob.repository;

import com.example.emob.entity.DeliveryItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, UUID> {
  List<DeliveryItem> findByDelivery_Id(UUID deliveryId);

  @Query(
      value =
          "SELECT COUNT(*) FROM delivery_item WHERE delivery_id = :deliveryId"
              + "                                AND status <> 'DELIVERED'",
      nativeQuery = true)
  long countNotDeliveredNative(@Param("deliveryId") UUID deliveryId);

  boolean existsByVehicleUnit_Id(UUID vehicleId);
}
