package com.awesomepizza.ordering.internal.dto.administration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@Schema(name = "PageResponse", description = "A page of results and its pagination metadata")
public class PageResponseDTO<T> {
    List<T> content;
    int page;
    int size;
    long totalElements;
    int totalPages;
    boolean first;
    boolean last;
}
