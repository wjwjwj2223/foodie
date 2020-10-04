package com.test;

import com.imooc.Application;
import com.imooc.es.pojo.Stu;
import org.assertj.core.data.Index;
import org.elasticsearch.action.index.IndexRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ESTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    //不建议使用 elasticsearchTemplate 对索引进行管理(创建索引, 更新索引, 删除索引)
    //索引就像是数据库或者数据库中的表 平时不会频繁的通过Java代码创建更新数据库, 我们只会
    // 进行CRUD操作, 在es也是同理 我们尽量只是用 ElasticsearchTemplate进行crud操作
    /*1. 属性(Fieldtype 不灵活)
      2. 主分片和副本分片数无法设置
    * */
    //创建更新索引
    @Test
    public void createIndexStu() {
        Stu stu = new Stu();
        stu.setStuId(1002L);
        stu.setName("spider name");
        stu.setAge(19);
        stu.setDescription("spider");
        stu.setSign("sign");
        IndexQuery build = new IndexQueryBuilder().withObject(stu).build();
        elasticsearchTemplate.index(build);
    }

    //删除索引
    @Test
    public void deleteIndexStu() {
        elasticsearchTemplate.deleteIndex(Stu.class);
    }

    //---------------------------------------
    //文档数据的修改
    @Test
    public void updateStuDoc() {
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("sign", "i am not superMan");
        sourceMap.put("age", "100");
        IndexRequest request = new IndexRequest();
        request.source(sourceMap);
        UpdateQuery updateQuery = new UpdateQueryBuilder()
                .withClass(Stu.class)
                .withId("1002")
                .withIndexRequest(request)
                .build();
        elasticsearchTemplate.update(updateQuery);
    }

    //文档数据的查询
    @Test
    public void getStuDoc() {
        GetQuery getQuery = new GetQuery();
        getQuery.setId("1002");
        Stu stu = elasticsearchTemplate.queryForObject(getQuery, Stu.class);
        System.out.println(stu.toString());
    }

    // 删除文档数据
    @Test
    public void deleteStuDoc() {
        elasticsearchTemplate.delete(Stu.class, "1002");
    }

}
