package com.manager.repository;

import com.manager.po.Bank;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BankRepository extends MongoRepository<Bank, Long> {
}
