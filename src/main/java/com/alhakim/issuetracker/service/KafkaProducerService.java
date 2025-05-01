package com.alhakim.issuetracker.service;

public interface KafkaProducerService {
    void publishIssueIndex(String message);
}
