package com.my.service.impl;

import com.my.pojo.Items;
import com.my.pojo.Stu;
import com.my.service.ItemsEsService;
import com.my.utils.PagedGridResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Arthur xzw
 * @Date 2021/3/8 22:52
 * @Description
 */
@Service
public class ItemsEsServiceImpl implements ItemsEsService {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        String preTag = "<font color='red'>";
        String postTag = "</font>";

        SortBuilder sortBuilder = null;
        if (sort.equals("c")) {
            sortBuilder = new FieldSortBuilder("sellCounts")
                    .order(SortOrder.DESC);
        } else if (sort.equals("p")) {
            sortBuilder = new FieldSortBuilder("price")
                    .order(SortOrder.ASC);
        } else {
            sortBuilder = new FieldSortBuilder("itemName.keyword")
                    .order(SortOrder.ASC);
        }

        String itemNameFiled = "itemName";

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(itemNameFiled,keywords))
                .withHighlightFields(new HighlightBuilder.Field(itemNameFiled))
                .withPageable(pageable)
                .withSort(sortBuilder)
                .build();

        AggregatedPage<Items> result =  elasticsearchTemplate.queryForPage(searchQuery, Items.class, new SearchResultMapper() {
                    @Override
                    public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                        List<Items> itemsList = new ArrayList<>();
                        SearchHits hits = searchResponse.getHits();
                        for(SearchHit hit:hits){
                            HighlightField highlightField = hit.getHighlightFields().get(itemNameFiled);
                            String itemName = highlightField.getFragments()[0].toString();
                            String itemId = (String) hit.getSourceAsMap().get("itemId");
                            String imgUrl =(String)hit.getSourceAsMap().get("imgUrl");
                            Integer price = (Integer)hit.getSourceAsMap().get("price");
                            Integer sellCounts = (Integer)hit.getSourceAsMap().get("sellCounts");
                            Items itemsHL = new Items();

                            itemsHL.setItemId(itemId);
                            itemsHL.setImgUrl(imgUrl);
                            itemsHL.setItemName(itemName);
                            itemsHL.setPrice(price);
                            itemsHL.setSellCounts(sellCounts);
                            itemsList.add(itemsHL);
                        }

                        return new AggregatedPageImpl<>((List<T>)itemsList,pageable,searchResponse.getHits().totalHits);

                    }
                }
        );

        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setRows(result.getContent());
        pagedGridResult.setPage(page + 1);
        pagedGridResult.setTotal(result.getTotalPages());
        pagedGridResult.setRecords(result.getTotalElements());
        return pagedGridResult;
    }
}
