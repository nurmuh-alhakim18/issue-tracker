package com.alhakim.issuetracker.dto;

import com.alhakim.issuetracker.entity.Issue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class IssueRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be in between 3-255 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    @NotNull(message = "Priority is required")
    private Issue.Priority priority;

    private List<String> tags;
}
