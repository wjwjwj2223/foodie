package com.test;

import com.imooc.Application;
import com.imooc.es.pojo.Stu;
import org.assertj.core.data.Index;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.facet.FacetResult;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.function.Function;

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
        stu.setStuId(1002);
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

    //------------------------------

    @Test
    public void searchStuDoc() {
        Pageable pageable = PageRequest.of(0,2);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders
                        .matchQuery("description", "sa"))
                .withPageable(pageable)
                .build();

        AggregatedPage<Stu> stus = elasticsearchTemplate
                .queryForPage(searchQuery, Stu.class);
        System.out.println("检索后的总分页数:" + stus.getTotalPages());
        List<Stu> stuList = stus.getContent();
        for (Stu s : stuList) {
            System.out.println(s.toString());
        }
    }

    //高亮搜索
    @Test
    public void highlightSearchStuDoc() {
        String pretag = "<font color='red'>";
        String postTag = "</font>";
        Pageable pageable = PageRequest.of(0,2);
        SortBuilder ageBuilder = new FieldSortBuilder("age")
                .order(SortOrder.ASC);
        SortBuilder stuIdBuilder = new FieldSortBuilder("stuId")
                .order(SortOrder.DESC);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders
                        .matchQuery("description", "spider"))
                .withHighlightFields(new HighlightBuilder
                        .Field("description")
                        .preTags(pretag)
                        .postTags(postTag))
                .withSort(ageBuilder)
                .withSort(stuIdBuilder)
                .withPageable(pageable)
                .build();

        AggregatedPage<Stu> stus = elasticsearchTemplate
                .queryForPage(searchQuery, Stu.class,
                        new SearchResultMapper() {
                    @Override
                    public <T> AggregatedPage<T> mapResults(
                            SearchResponse searchResponse,
                            Class<T> aClass, Pageable pageable) {
                        List<Stu> stuList = new ArrayList<>();
                        SearchHits hits = searchResponse.getHits();
                        for (SearchHit hit : hits) {
                            Integer stuId = (Integer) hit.getSourceAsMap().get("stuId");
                            String name = (String) hit.getSourceAsMap().get("name");
                            Integer age = Integer.valueOf((String) hit.getSourceAsMap().get("age"));
                            String sign = (String) hit.getSourceAsMap().get("sign");
                            HighlightField field = hit.getHighlightFields().get("description");
                            String description = field.getFragments()[0].toString();
                            Stu highStudent = new Stu();
                            highStudent.setDescription(description);
                            highStudent.setStuId(stuId);
                            highStudent.setName(name);
                            highStudent.setAge(age);
                            highStudent.setSign(sign);
                            stuList.add(highStudent);
                        }
                        if (stuList.size() > 0) {
                            return new AggregatedPageImpl<>((List<T>) stuList);
                        }
                        return null;
                    }
                });
        System.out.println("检索后的总分页数:" + stus.getTotalPages());
        List<Stu> stuList = stus.getContent();
        for (Stu s : stuList) {
            System.out.println(s.toString());
        }
    }

}
