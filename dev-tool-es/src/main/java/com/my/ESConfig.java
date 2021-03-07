package com.my;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Arthur xzw
 * @Date 2021/3/7 22:26
 * @Description
 */
@Configuration
public class ESConfig {
    /**
     * 解决netty引起的issue
     */
    @PostConstruct
    void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
}
