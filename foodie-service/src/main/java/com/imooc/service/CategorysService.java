package com.imooc.service;

import com.imooc.pojo.Categorys;
import com.imooc.pojo.vo.CategoryVO;

import java.util.List;

public interface CategorysService {

    /**
     * 查询所有一级分类
     * @return
     */
    List<Categorys> queryAllRootLevelCat();

    /**
     * 根据父id  查询子分类
     * @param rootCatId
     * @return
     */
    public List<CategoryVO> getSubCatList(Integer rootCatId);

}
