package com.imooc;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//扫描 mybatis 通用mapper 所在的包
@MapperScan(basePackages = "com.imooc.mapper")
//扫描所有包 以及相关组件包
@ComponentScan(basePackages = {"com.imooc", "org.n3r.idworker"})
//开启使用redis 作为spring session
@EnableRedisHttpSession
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
