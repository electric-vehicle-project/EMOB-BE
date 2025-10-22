/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.*;
import com.example.emob.model.response.SaleOrder.SaleOrderItemResponse;
import com.example.emob.model.response.SaleOrder.SaleOrderResponse;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SaleOrderMapper {

  // ==========================
  // 🔹 ENTITY → RESPONSE
  // ==========================
  @Mapping(source = "dealer.id", target = "dealerId")
  @Mapping(source = "account.id", target = "accountId")
  @Mapping(source = "customer.id", target = "customerId")
  SaleOrderResponse toSaleOrderResponse(SaleOrder saleOrder);

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

  // ==========================
  // 🔹 VEHICLE REQUEST → SALE ORDER
  // ==========================
  @Mapping(
      target = "saleOrderItems",
      expression = "java(toSaleOrderItems(vehicleRequest.getVehicleRequestItems()))")
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "account", ignore = true)
  @Mapping(target = "vehicleRequest", source = "vehicleRequest")
  @Mapping(target = "status", ignore = true)
  SaleOrder toSaleOrder(VehicleRequest vehicleRequest);

  default Set<SaleOrderItem> toSaleOrderItems(Set<VehicleRequestItem> vehicleRequestItems) {
    if (vehicleRequestItems == null) return new HashSet<>();

    return vehicleRequestItems.stream()
        .filter(vri -> !vri.isDeleted()) // ✅ bỏ qua item bị xóa
        .map(
            vri -> {
              SaleOrderItem item = new SaleOrderItem();
              item.setUnitPrice(vri.getUnitPrice());
              item.setTotalPrice(vri.getTotalPrice());
              item.setDiscountPrice(BigDecimal.ZERO);
              item.setQuantity(vri.getQuantity());
              item.setColor(vri.getColor());
              item.setVehicleStatus(vri.getVehicleStatus());
              item.setVehicle(vri.getVehicle());
              item.setDeleted(false); // mặc định là active
              return item;
            })
        .collect(Collectors.toSet());
  }
}
