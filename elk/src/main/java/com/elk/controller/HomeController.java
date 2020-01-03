package com.elk.controller;

import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @RequestMapping("/hello")
    public String index() {
        return "hello world !";
    }

    @RequestMapping("/count")
    public long test() {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("bank")
                .withTypes("_doc")
                .withQuery(new MatchAllQueryBuilder())
                .build();
        long count = elasticsearchRestTemplate.count(searchQuery);

        return count;
    }

}
