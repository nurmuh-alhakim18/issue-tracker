package com.alhakim.issuetracker.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        return ElasticsearchClient.of(ec -> ec.host(elasticsearchUrl));
    }
}
