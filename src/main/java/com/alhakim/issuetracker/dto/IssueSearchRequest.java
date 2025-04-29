package com.alhakim.issuetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueSearchRequest {
    private String query;
    private String status;
    private String priority;
    private List<String> tags;
    private String orderBy;
    private String direction;
    private Integer page;
    private Integer size;
}
