package com.elk.repository;

import com.elk.po.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends EsBaseRepository<User, Long> {

}
