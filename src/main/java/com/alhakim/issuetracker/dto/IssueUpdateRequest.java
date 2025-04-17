package com.alhakim.issuetracker.dto;

import com.alhakim.issuetracker.entity.Issue;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueUpdateRequest {

    @Size(min = 3, max = 50, message = "Title must be in between 3-255 characters")
    private String title;

    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    private Issue.Priority priority;

    private Issue.Status status;

    private List<String> tags;
}
