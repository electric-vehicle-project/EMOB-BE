package com.example.emob.mapper;

import com.example.emob.model.response.PageResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.function.Function;

@Mapper(componentModel = "spring")
public interface PageMapper {
    default <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        return PageResponse.<R>builder()
                .content(page.getContent().stream().map(mapper).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
