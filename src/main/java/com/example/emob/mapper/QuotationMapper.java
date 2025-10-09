
package com.example.emob.mapper;

import com.example.emob.entity.Quotation;
import com.example.emob.model.request.quotation.QuotationRequest;
import com.example.emob.model.response.QuotationResponse;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface QuotationMapper {
    Quotation toQuotation(QuotationRequest request);
    QuotationResponse toQuotationResponse(Quotation account);
}

