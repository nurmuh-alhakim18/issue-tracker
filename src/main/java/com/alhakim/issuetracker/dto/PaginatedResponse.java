package com.alhakim.issuetracker.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
public class PaginatedResponse<T> {
    private List<T> data;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private long totalPages;
    private boolean last;

    public static <T> PaginatedResponse<T> create(Page<T> page) {
        return PaginatedResponse.<T>builder()
                .data(page.getContent())
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .last(page.isLast())
                .build();
    }

    public static <T> PaginatedResponse<T> create(List<T> data , PageInfo pageInfo) {
        return PaginatedResponse.<T>builder()
                .data(data)
                .pageNo(pageInfo.getPageNo())
                .pageSize(pageInfo.getPageSize())
                .totalPages(pageInfo.getTotalPages())
                .totalElements(pageInfo.getTotalElements())
                .last(pageInfo.isLast())
                .build();
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageInfo {
        private int pageNo;
        private int pageSize;
        private long totalElements;
        private long totalPages;
        private boolean last;
    }
}
