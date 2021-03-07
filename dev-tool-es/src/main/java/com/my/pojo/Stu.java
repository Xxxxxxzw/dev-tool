package com.my.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @Arthur xzw
 * @Date 2021/3/7 22:32
 * @Description
 */
@Data
@Document(indexName = "stu",type = "_doc")
public class Stu {

    @Id
    private Long stuId;

    @Field(store = true)
    private String name;

    @Field(store = true)
    private Integer age;

    @Field(store = true)
    private String description;

    @Field(store = true)
    private float money;
}
