package com.imooc.service;

import com.imooc.pojo.Categorys;

import java.util.List;

public interface CategorysService {

    /**
     * 查询所有一级分类
     * @return
     */
    List<Categorys> queryAllRootLevelCat();

}
