package com.my.service;

import com.my.utils.PagedGridResult;

/**
 * @Arthur xzw
 * @Date 2021/3/8 22:51
 * @Description
 */
public interface ItemsEsService {

    PagedGridResult searchItems(String keywords,String sort, Integer page, Integer pageSize);
}
