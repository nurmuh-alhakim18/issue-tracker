package com.alhakim.issuetracker.service;

public interface KafkaConsumerService {
    void listenIssueIndex(String message);
}
