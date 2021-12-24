package elk;

import com.App;
import com.elk.po.User;
import com.elk.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = App.class)
@RunWith(SpringRunner.class)
public class ElasticSearchTest {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchTest.class);

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void create() {
        elasticsearchRestTemplate.createIndex(User.class);
    }

    @Test
    public void save() {
        User user = new User();
        user.setId(4l);
        user.setUsername("mrwang");
        user.setPassword("123456");
        user.setAge(30);
        userRepository.save(user);
    }

    @Test
    public void findAll() {
        userRepository.findAll().forEach(user -> {
            try {
                String text = objectMapper.writeValueAsString(user);
                logger.info(text);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

    }

    @Test
    public void count(){
//        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withIndices("bank")
//                .withTypes("_doc")
//                .withQuery(new MatchAllQueryBuilder())
//                .build();
//        long count = elasticsearchRestTemplate.count(searchQuery);
//        logger.info("索引es_doc中有{}个文档", count);
    }

}
