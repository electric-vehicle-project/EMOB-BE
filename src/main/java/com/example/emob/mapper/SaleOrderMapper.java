/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.*;
import com.example.emob.model.response.SaleOrder.SaleOrderItemResponse;
import com.example.emob.model.response.SaleOrder.SaleOrderResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SaleOrderMapper {

  // ==========================
  // 🔹 ENTITY → RESPONSE
  // ==========================
  @Mapping(source = "account.id", target = "accountId")
  @Mapping(source = "vehicleRequest.dealer.id", target = "dealerId")
  @Mapping(source = "quotation.customer.id", target = "customerId")
  SaleOrderResponse toSaleOrderResponse(SaleOrder saleOrder);

  @Mapping(source = "vehicle.id", target = "vehicleId")
  @Mapping(source = "promotion.id", target = "promotionId")
  @Mapping(target = "vehicleUnitIds", expression = "java(mapVehicleUnitIds(item))")
  SaleOrderItemResponse toSaleOrderItemResponse(SaleOrderItem item);

  default Set<SaleOrderItemResponse> filterActiveItems(Set<SaleOrderItem> items) {
    if (items == null) return Collections.emptySet();
    return items.stream()
        .filter(item -> !item.isDeleted()) // ✅ chỉ map item chưa xóa
        .map(this::toSaleOrderItemResponse)
        .collect(Collectors.toSet());
  }

  @AfterMapping
  default void afterMapping(@MappingTarget SaleOrderResponse response, SaleOrder saleOrder) {
    response.setItems(filterActiveItems(saleOrder.getSaleOrderItems()));
  }

  default Set<UUID> mapVehicleUnitIds(SaleOrderItem item) {
    if (item == null || item.getVehicleUnits() == null || item.getVehicleUnits().isEmpty()) {
      return Collections.emptySet();
    }
    return item.getVehicleUnits().stream()
        .map(VehicleUnit::getId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  // ==========================
  // 🔹 VEHICLE REQUEST → SALE ORDER
  // ==========================
  @Mapping(target = "account", ignore = true)
  @Mapping(target = "vehicleRequest", source = "vehicleRequest")
  @Mapping(target = "status", ignore = true)
  SaleOrder toSaleOrderFromVehicleRequest(VehicleRequest vehicleRequest);


}
