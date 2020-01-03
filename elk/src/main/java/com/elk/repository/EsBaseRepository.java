package com.elk.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EsBaseRepository<T, ID> extends ElasticsearchRepository<T, ID> {
}
