package com.imooc.controller;

import com.imooc.service.ItemESService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("items")
public class ItemsController {

    @Autowired
    private ItemESService itemESService;

    @GetMapping("/hello")
    public Object hello() {
        return "Hello ElasticSearch~";
    }

    @GetMapping("/es/search")
    public IMOOCJSONResult search(
            String keywords,
            String sort,
            Integer page,
            Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 20;
        }

        page --;

        PagedGridResult grid = itemESService.searhItems(keywords,
                sort,
                page,
                pageSize);
        return IMOOCJSONResult.ok(grid);
    }

}
