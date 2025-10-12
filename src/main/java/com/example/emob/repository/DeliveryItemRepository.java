package com.example.emob.repository;

import com.example.emob.entity.DeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, UUID> {
    List<DeliveryItem> findByDelivery_Id (UUID deliveryId);

}
