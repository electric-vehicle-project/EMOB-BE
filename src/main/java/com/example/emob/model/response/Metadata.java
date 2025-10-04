package com.example.emob.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Metadata {
    int page;
    int size;
    long totalElements;
    int totalPages;
    boolean last;
}
