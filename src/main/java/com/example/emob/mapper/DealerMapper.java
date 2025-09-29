package com.example.emob.mapper;

import com.example.emob.entity.Dealer;
import com.example.emob.model.request.DealerRequest;
import com.example.emob.model.response.DealerResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface DealerMapper {
    DealerResponse toDealerResponse (Dealer dealer);
    Dealer toDealer (DealerRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Dealer updateDealer(DealerRequest request, @MappingTarget Dealer dealer);
}
