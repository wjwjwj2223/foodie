package com.imooc.service;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;

import java.util.List;

public interface ItemService {

    /**
     * 根据商品id 查询详情
     * @param id
     * @return
     */
    public Items queryItemById(String id);

    /**
     * 根据商品id  查询商品图片列表
     * @param itemId
     * @return
     */
    public List<ItemsImg> queryItemImgListById(String itemId);

    /**
     * 根据商品id  查询商品规格
     * @param itemId
     * @return
     */
    public List<ItemsSpec> queryItemSpecListById(String itemId);

    /**
     * 根据商品id  查询商品参数
     * @param itemId
     * @return
     */
    public ItemsParam queryItemParamById(String itemId);

}
