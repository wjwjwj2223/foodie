package com.imooc.service;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.utils.PagedGridResult;

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

    /**
     * 根据商品id查询商品的评价等级数量
     * @param itemId
     */
    public CommentLevelCountsVO queryCommentCounts(String itemId);

    /**
     * 根据商品id查询商品的评价（分页）
     * @param itemId
     * @param level
     * @return
     */
    public PagedGridResult queryPagedComments(String itemId, Integer level,
                                              Integer page, Integer pageSize);

    /**
     * 搜索商品列表
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searhItems(String keywords, String sort,
                                      Integer page, Integer pageSize);

    /**
     * 根据分类id搜索商品列表
     * @param catId
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searhItems(Integer catId, String sort,
                                      Integer page, Integer pageSize);
}
