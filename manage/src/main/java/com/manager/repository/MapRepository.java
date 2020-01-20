package com.manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Map;

public interface MapRepository extends MongoRepository<Map, Long> {
}
