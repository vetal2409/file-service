package com.sigma.university.cloud.sample;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class ElasticsearchClientConfiguration {

    @Value("${elasticsearch.url}")
    private String esUrl;

    @Bean
    public RestHighLevelClient esClient() {
        return new RestHighLevelClient(
                RestClient.builder(HttpHost.create(esUrl))
        );
    }
}
