
package com.example.emob.model.response;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuotationResponse {
    String text;
}

