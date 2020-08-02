package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Categorys;
import com.imooc.service.CarouselService;
import com.imooc.service.CategorysService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
