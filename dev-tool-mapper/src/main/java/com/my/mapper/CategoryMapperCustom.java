package com.my.mapper;

import com.my.pojo.vo.CategoryVO;

import java.util.List;

/**
 * @Author xzw
 * @Date 2020/6/18
 */
public interface CategoryMapperCustom {
    public List<CategoryVO> getSubCatList(Integer rootCatId);
}
