package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.dto.IssueRequest;
import com.alhakim.issuetracker.dto.IssueResponse;
import com.alhakim.issuetracker.dto.IssueUpdateRequest;

import java.util.List;

public interface IssueService {
    void createIssue(IssueRequest issueRequest, Long userId);
    List<IssueResponse> getIssues();
    IssueResponse getIssue(Long issueId);
    void updateIssue(Long issueId, IssueUpdateRequest issueUpdateRequest);
    void deleteIssue(Long issueId);
}
