/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.entity.Customer;
import com.example.emob.entity.Dealer;
import com.example.emob.model.request.CustomerRequest;
import com.example.emob.model.request.DealerRequest;
import com.example.emob.model.response.CustomerResponse;
import com.example.emob.model.response.DealerResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponse toCustomerResponse(Customer customer);

    Customer toCustomer(CustomerRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Customer updateCustomer(CustomerRequest request, @MappingTarget Customer customer);
}
