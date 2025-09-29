package com.example.emob.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
     List<T> content;
     int page;
     int size;
     long totalElements;
     int totalPages;
     boolean last;
}
