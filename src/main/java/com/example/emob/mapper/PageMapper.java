/* EMOB-2025 */
package com.example.emob.mapper;

import com.example.emob.model.response.Metadata;
import com.example.emob.model.response.PageResponse;
import java.util.function.Function;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PageMapper {

    default <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        return PageResponse.<R>builder()
                .data(page.getContent().stream().map(mapper).toList())
                .metadata(
                        Metadata.builder()
                                .page(page.getNumber())
                                .size(page.getSize())
                                .totalElements(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .last(page.isLast())
                                .build())
                .build();
    }
}
