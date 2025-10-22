/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.SaleContract;
import com.example.emob.entity.SaleOrder;
import com.example.emob.model.response.ContractResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContractMapper {
  @Mapping(source = "id", target = "contractId")
  @Mapping(source = "saleOrder.id", target = "orderId")
  ContractResponse toContractResponse(SaleContract contract);

//  @Mapping(target = "id", ignore = true)
//  SaleContract toSaleContract(SaleOrder order);
}
