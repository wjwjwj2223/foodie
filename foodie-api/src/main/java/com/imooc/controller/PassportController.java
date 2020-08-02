package com.imooc.controller;

import com.imooc.pojo.User;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "登录注册", tags = {"用于登录注册的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username) {
        //1. 判断用户名不能为空
        if (StringUtils.isBlank(username)){
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }
        //2. 查找注册的用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }
        //3. 请求成功用户名没有重复
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户名注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBO) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPwd = userBO.getConfirmPassword();
        //0 判断用户名和密码不能为空
        if (StringUtils.isBlank(username)
                || StringUtils.isBlank(password)
                || StringUtils.isBlank(confirmPwd)) {
            return IMOOCJSONResult.errorMsg("用户名或者密码不能为空");
        }
        //1 密码长度不能少于6位
        if (password.length() < 6) {
            return IMOOCJSONResult.errorMsg("密码长度不能小于6");
        }
        //2 两次密码一致
        if (!password.equals(confirmPwd)) {
            return IMOOCJSONResult.errorMsg("两次密码输入不一致");
        }
        //3 查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }
        //4 注册
        userService.createUser(userBO);
        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        //0 判断用户名和密码不能为空
        if (StringUtils.isBlank(username)
                || StringUtils.isBlank(password)) {
            return IMOOCJSONResult.errorMsg("用户名或者密码不能为空");
        }
        //1 实现登录
        User user = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (user == null) {
            return IMOOCJSONResult.errorMsg("用户名或者密码不正确");
        }
        User userResult = setNullProperty(user);
        CookieUtils.setCookie(request, response,"user", JsonUtils.objectToJson(userResult), true);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId, HttpServletRequest request, HttpServletResponse response) {

        //清除相关cookie
        CookieUtils.deleteCookie(request, response, "user");
        // TODO 用户退出登陆需要清空购物车
        // TODO 分布式会话中需要清空用户数据
        return IMOOCJSONResult.ok();
    }


    private User setNullProperty(User user) {
        user.setPassword(null);
        user.setRealname(null);
        user.setCreatedTime(null);
        user.setUpdatedTime(null);
        user.setEmail(null);
        return user;
    }

}
