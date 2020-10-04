package com.test;

import com.imooc.Application;
import com.imooc.es.pojo.Stu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ESTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void createIndexStu() {
        Stu stu = new Stu();
        stu.setStuId(1001L);
        stu.setName("bat name");
        stu.setAge(18);
        IndexQuery build = new IndexQueryBuilder().withObject(stu).build();
        elasticsearchTemplate.index(build);
    }

}
