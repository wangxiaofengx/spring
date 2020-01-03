package com.elk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories
public class ElasticsearchConfig {

//    @Bean
//    RestHighLevelClient client() {
//
//        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo("192.168.1.200:9200")
//                .build();
//
//        return RestClients.create(clientConfiguration).rest();
//    }

}
