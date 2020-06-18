package com.my.service;

import com.my.pojo.Carousel;

import java.util.List;

/**
 * @Author xzw
 * @Date 2020/6/18
 */
public interface CarouselService {
    List<Carousel> queryAll(Integer isShow);
}
