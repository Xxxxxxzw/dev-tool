package com.my.mapper;

import com.my.pojo.vo.CategoryVO;
import com.my.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author xzw
 * @Date 2020/6/18
 */
public interface CategoryMapperCustom {
    public List<CategoryVO> getSubCatList(Integer rootCatId);

    public List<NewItemsVO> getSixNewItemsLazy(@Param("paramsMap")Map<String,Object> map);
}
