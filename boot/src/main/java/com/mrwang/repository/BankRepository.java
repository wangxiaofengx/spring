package com.mrwang.repository;

import com.mrwang.po.Bank;

import java.util.List;

public interface BankRepository extends BaseRepository<Bank, Long> {

    public List<Bank> findByFirstnameLikeOrLastnameLike(String firstname, String lastname);

    List<Bank> findByAgeBetween(Integer start, Integer end);
    List<Bank> findByAgeBetweenOrderByAgeDesc(Integer start, Integer end);
}
