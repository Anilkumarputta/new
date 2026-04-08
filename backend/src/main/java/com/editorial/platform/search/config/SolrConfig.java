package com.editorial.platform.search.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrConfig {

    @Bean
    public SolrClient solrClient(
        @Value("${solr.base-url}") String baseUrl,
        @Value("${solr.core}") String core
    ) {
        return new HttpSolrClient.Builder(baseUrl + "/" + core).build();
    }
}
