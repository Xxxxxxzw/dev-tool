package com.my.service;

import com.my.pojo.Category;
import com.my.pojo.vo.CategoryVO;
import com.my.pojo.vo.NewItemsVO;

import java.util.List;

/**
 * @Author xzw
 * @Date 2020/6/18
 */
public interface CategoryService {
    List<Category> queryAllRootLevelCat();

    List<CategoryVO> getSubCatList(Integer rootCatId);

    List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId);
}
