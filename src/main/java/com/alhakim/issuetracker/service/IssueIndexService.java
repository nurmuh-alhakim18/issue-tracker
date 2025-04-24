package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.entity.Issue;

import java.io.IOException;

public interface IssueIndexService {
    void bulkIndexIssues() throws IOException;
    void indexIssue(Issue issue);
    void deleteIssue(Issue issue);
}
