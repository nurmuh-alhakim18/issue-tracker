package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.dto.IssueRequest;
import com.alhakim.issuetracker.dto.IssueResponse;
import com.alhakim.issuetracker.dto.IssueUpdateRequest;
import com.alhakim.issuetracker.dto.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IssueService {
    void createIssue(IssueRequest issueRequest, Long userId);
    PaginatedResponse<IssueResponse> getIssues(Pageable pageable);
    IssueResponse getIssue(Long issueId);
    void updateIssue(Long issueId, IssueUpdateRequest issueUpdateRequest);
    void deleteIssue(Long issueId);
}
