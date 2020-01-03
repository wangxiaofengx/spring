package com.elk.repository;

import com.elk.po.Bank;

import java.util.List;

public interface BankRepository extends EsBaseRepository<Bank, Long> {

    List<Bank> findByFirstnameLikeOrLastnameLike(String firstname, String lastname);

    List<Bank> findByAgeBetween(Integer start, Integer end);
    List<Bank> findByAgeBetweenOrderByAgeDesc(Integer start, Integer end);
}
