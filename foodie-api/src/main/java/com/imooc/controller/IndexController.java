package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Categorys;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;
import com.imooc.service.CarouselService;
import com.imooc.service.CategorysService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping(value = "index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategorysService categorysService;

    @Transactional(propagation = Propagation.SUPPORTS)
    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public IMOOCJSONResult carousel() {
        List<Carousel> carousels = carouselService.queryAll(YesOrNo.YES.type);
        return IMOOCJSONResult.ok(carousels);
    }


    /**
     * 首页分类展示需求
     * 1. 第一次刷新主页查询大分类， 渲染展示到主页
     * 2. 鼠标移到大分类, 加载子分类, 且只会加载一次
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @ApiOperation(value = "获取商品一级分类", notes = "获取商品一级分类", httpMethod = "GET")
    @GetMapping("/cats")
    public IMOOCJSONResult cats() {
        List<Categorys> categories = categorysService.queryAllRootLevelCat();
        return IMOOCJSONResult.ok(categories);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public IMOOCJSONResult subCat(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return IMOOCJSONResult.errorMsg("分类不存在");
        }

        List<CategoryVO> list = categorysService.getSubCatList(rootCatId);
        return IMOOCJSONResult.ok(list);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes = "查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public IMOOCJSONResult sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return IMOOCJSONResult.errorMsg("分类不存在");
        }

        List<NewItemsVO> list = categorysService.getSixNewItemsLazy(rootCatId);
        return IMOOCJSONResult.ok(list);
    }

}
