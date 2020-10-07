package com.imooc.service.impl;

import com.imooc.es.pojo.Items;
import com.imooc.es.pojo.Stu;
import com.imooc.service.ItemESService;
import com.imooc.utils.PagedGridResult;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.ArrayList;
import java.util.List;

public class ItemESServiceImpl implements ItemESService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public PagedGridResult searhItems(String keywords, String sort, Integer page, Integer pageSize) {
        String pretag = "<font color='red'>";
        String postTag = "</font>";
        Pageable pageable = PageRequest.of(page,pageSize);
        SortBuilder builder = null;
        if (sort.equals("c")) {
            builder = new FieldSortBuilder("money")
                    .order(SortOrder.ASC);
        }

        String ItemNameField = "itemName";
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders
                        .matchQuery(ItemNameField, keywords))
                .withHighlightFields(new HighlightBuilder
                        .Field(ItemNameField)
                        .preTags(pretag)
                        .postTags(postTag))
                .withSort(builder)
                .withPageable(pageable)
                .build();

        AggregatedPage<Items> pagedItems = elasticsearchTemplate
                .queryForPage(searchQuery,
                        Items.class,
                        new SearchResultMapper() {
                            @Override
                            public <T> AggregatedPage<T> mapResults(
                                    SearchResponse searchResponse,
                                    Class<T> aClass, Pageable pageable) {
                                List<Items> itemsList = new ArrayList<>();
                                SearchHits hits = searchResponse.getHits();
                                for (SearchHit hit : hits) {
                                    HighlightField field = hit.getHighlightFields().get(ItemNameField);
                                    String itemsName = field.getFragments()[0].toString();
                                    String itemId = (String) hit.getSourceAsMap().get("itemId");
                                    String imgUrl = (String) hit.getSourceAsMap().get("imgUrl");
                                    Integer price = Integer.valueOf((String) hit.getSourceAsMap().get("price"));
                                    Integer sellCounts = Integer.valueOf((String) hit.getSourceAsMap().get("sellCounts"));



                                    Items highItems = new Items();
                                    highItems.setItemId(itemId);
                                    highItems.setItemName(itemsName);
                                    highItems.setImgUrl(imgUrl);
                                    highItems.setPrice(price);
                                    highItems.setSellCounts(sellCounts);
                                    itemsList.add(highItems);
                                }
                                return new AggregatedPageImpl<>((List<T>) itemsList,
                                        pageable,
                                        searchResponse.getHits().totalHits);
                            }
                        });
        PagedGridResult result = new PagedGridResult();
        result.setRows(pagedItems.getContent());
        result.setPage(page + 1);
        result.setTotal(pagedItems.getTotalPages());
        result.setRecords(pagedItems.getTotalElements());
        return result;
    }
}
