/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.SaleContract;
import com.example.emob.entity.SaleContractItem;
import com.example.emob.entity.SaleOrderItem;
import com.example.emob.entity.VehicleUnit;
import com.example.emob.model.response.saleContract.ContractItemResponse;
import com.example.emob.model.response.saleContract.ContractResponse;
import java.util.*;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContractMapper {

  // 🔹 Map từng SaleOrderItem → SaleContractItem
  @Mapping(source = "vehicle", target = "vehicle")
  @Mapping(source = "promotion", target = "promotion")
  @Mapping(source = "vehicleStatus", target = "vehicleStatus")
  @Mapping(source = "quantity", target = "quantity")
  @Mapping(source = "color", target = "color")
  @Mapping(source = "unitPrice", target = "unitPrice")
  @Mapping(source = "discountPrice", target = "discountPrice")
  @Mapping(source = "totalPrice", target = "totalPrice")
  SaleContractItem toSaleContractItem(SaleOrderItem orderItem);


  // 🔹 Map hợp đồng chính
  @Mapping(source = "id", target = "contractId")
  @Mapping(source = "saleOrder.id", target = "orderId")
  @Mapping(source = "contractNumber", target = "contractNumber")
  @Mapping(source = "status", target = "status")
  ContractResponse toContractResponse(SaleContract contract);

  // 🔹 Map từng item trong hợp đồng
  @Mapping(source = "vehicle.id", target = "vehicleId")
  @Mapping(source = "promotion.id", target = "promotionId")
  @Mapping(target = "vehicleUnitIds", expression = "java(mapVehicleUnitIds(item))")
  ContractItemResponse toContractItemResponse(SaleContractItem item);

  // 🔹 Lọc và map tất cả item chưa bị xóa (nếu có flag)
  default Set<ContractItemResponse> mapItems(Set<SaleContractItem> items) {
    if (items == null) return Collections.emptySet();
    return items.stream().map(this::toContractItemResponse).collect(Collectors.toSet());
  }

  // 🔹 Map danh sách vehicleUnitIds
  default Set<UUID> mapVehicleUnitIds(SaleContractItem item) {
    if (item == null || item.getVehicleUnits() == null || item.getVehicleUnits().isEmpty()) {
      return Collections.emptySet();
    }
    return item.getVehicleUnits().stream()
        .map(VehicleUnit::getId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  // 🔹 Sau khi map xong hợp đồng → gán danh sách item vào response
  @AfterMapping
  default void afterMapping(@MappingTarget ContractResponse response, SaleContract contract) {
    response.setItems(mapItems(contract.getSaleContractItems()));
  }
}
