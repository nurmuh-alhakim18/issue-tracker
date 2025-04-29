package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.dto.IssueResponse;
import com.alhakim.issuetracker.dto.IssueSearchRequest;
import com.alhakim.issuetracker.dto.PaginatedResponse;

public interface SearchService {
    PaginatedResponse<IssueResponse> search(IssueSearchRequest request);
}
