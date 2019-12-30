package com.mrwang.es;

import com.App;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrwang.ElasticSearchTest;
import com.mrwang.po.Bank;
import com.mrwang.repository.BankRepository;
import com.mrwang.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = App.class)
@RunWith(SpringRunner.class)
public class BankTest {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchTest.class);

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BankRepository bankRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void findAll() throws JsonProcessingException {
        Iterable<Bank> resultList = bankRepository.findAll(PageRequest.of(1,10));
        for (Bank bank : resultList) {
            String info = objectMapper.writeValueAsString(bank);
            logger.info(info);
        }
    }

    @Test
    public void query(){
        bankRepository.findByFirstnameLikeOrLastnameLike("Jimenez","s").forEach(bank -> {
            try {
                String info = objectMapper.writeValueAsString(bank);
                logger.info(info);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });
//        bankRepository.search()
    }
    @Test
    public void between(){
        bankRepository.findByAgeBetweenOrderByAgeDesc(20,25).forEach(bank -> {
            try {
                String info = objectMapper.writeValueAsString(bank);
                logger.info(info);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });
    }
}
