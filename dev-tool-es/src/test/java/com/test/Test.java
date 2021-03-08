package com.test;

import com.my.Application;
import com.my.pojo.Stu;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Arthur xzw
 * @Date 2021/3/7 22:30
 * @Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class Test {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    //建立索引
    @org.junit.Test
    public void indexEs(){
        Stu stu = new Stu();
        stu.setStuId(1002L);
        stu.setAge(34);
        stu.setDescription("good day!");
        stu.setName("李四");
        stu.setMoney(88F);
        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        elasticsearchTemplate.index(indexQuery);
    }

    //查找索引
    @org.junit.Test
    public void getIndex(){
        GetQuery getQuery = new GetQuery();
        getQuery.setId("1001");
        Stu stu = elasticsearchTemplate.queryForObject(getQuery,Stu.class);
        System.out.println(stu);
    }

    //修改
    @org.junit.Test
    public void update(){

        Map<String,Object> source = new HashMap<>();
        source.put("age",20);
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(source);

        UpdateQuery updateQuery = new UpdateQueryBuilder()
                .withId("1001")
                .withClass(Stu.class)
                .withIndexRequest(indexRequest)
                .build();
        elasticsearchTemplate.update(updateQuery);
    }

    //查询分页
    @org.junit.Test
    public void query(){
        Pageable pageable = PageRequest.of(0,2);
        String preTag = "<font color='red'>";
        String postTag = "</font>";
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description","day"))
                .withHighlightFields(new HighlightBuilder.Field("description").preTags(preTag).postTags(postTag))
                .withPageable(pageable)
                .withSort(SortBuilders.fieldSort("age").order(SortOrder.DESC))
                .build();

        AggregatedPage<Stu> result =  elasticsearchTemplate.queryForPage(searchQuery, Stu.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<Stu> stuList = new ArrayList<>();
                SearchHits hits = searchResponse.getHits();
                for(SearchHit hit:hits){
                    HighlightField highlightField = hit.getHighlightFields().get("description");
                    String description = highlightField.getFragments()[0].toString();
                    Object stuId = (Object) hit.getSourceAsMap().get("stuId");
                    String name =(String)hit.getSourceAsMap().get("name");
                    Integer age = (Integer)hit.getSourceAsMap().get("age");
                    Object money = (Object)hit.getSourceAsMap().get("money");
                    Stu stuHL = new Stu();

                    stuHL.setDescription(description);
                    stuHL.setStuId(Long.parseLong(stuId.toString()));
                    stuHL.setAge(age);
                    stuHL.setName(name);
                    stuHL.setMoney(Float.parseFloat(money.toString()));
                    stuList.add(stuHL);
                }
                if(stuList.size()>0){
                    return new AggregatedPageImpl<>((List<T>)stuList);
                }
                return null;
            }
        }
        );
        System.out.println(result.getTotalPages());
        List<Stu> list = result.getContent();
        for(Stu stu: list){
            System.out.println(stu);
        }
    }
    //高亮

}
