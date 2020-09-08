package com.imooc.controller;
import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@ApiIgnore
@RequestMapping("redis")
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public Object set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        return "set success";
    }

    @GetMapping("/get")
    public String get(String key) {
        return (String)redisTemplate.opsForValue().get(key);
    }

    @GetMapping("/delete")
    public Object delete(String key) {
        redisTemplate.delete(key);
        return "delete ok";
    }

    /**
     * 大量key查询
     * @param keys
     * @return
     */
    @GetMapping("/getALot")
    public Object getALot(String... keys) {
        List<String> resutl = new ArrayList<>();
        for (String k:keys) {
            resutl.add(redisOperator.get(k));
        }
        return resutl;
    }

    /**
     * 批量查询 mget
     * @param keys
     * @return
     */
    @GetMapping("/mget")
    public Object mget(String... keys) {
        List<String> keysList = Arrays.asList(keys);
        return redisOperator.mget(keysList);
    }

}
