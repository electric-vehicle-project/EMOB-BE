
package com.example.emob.model.request.quotation;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuotationRequest {
    List<QuotationItemRequest> requests;
}

