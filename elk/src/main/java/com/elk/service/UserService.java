package com.elk.service;

import com.elk.po.User;
import com.elk.repository.UserRepository;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    public UserService(UserRepository userRepository, ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.userRepository = userRepository;
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    public List<User> list() {
        return ImmutableList.copyOf(userRepository.findAll());
    }

    public User insert(User user) {
        return userRepository.save(user);
    }

    public void custom() {
        Criteria criteria = Criteria.where("");
        Query query = new CriteriaQuery(criteria);
        elasticsearchRestTemplate.search(query, User.class);
    }
}
