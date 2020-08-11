package com.my;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author xzw
 * @Date 2020/6/12
 */
@SpringBootApplication
@MapperScan(basePackages = "com.my.mapper")
@ComponentScan(basePackages = {"com.my","org.n3r.idworker"})
@EnableScheduling       // 开启定时任务
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
