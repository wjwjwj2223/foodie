package com.imooc.controller.interceptor;

import com.imooc.controller.BaseController;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class UserTokenInterceptor extends BaseController implements HandlerInterceptor {

    //在访问controller 调用之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("进入拦截器");
        String headerUserId = request.getHeader("headerUserId");
        String headerUserToken = request.getHeader("headerUserToken");
        if (StringUtils.isBlank(headerUserId) || StringUtils.isBlank(headerUserToken)) {
            returnErrorResponse(response, IMOOCJSONResult.errorMsg("请登录..."));
            return false;
        }
        String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + headerUserId);
        if (StringUtils.isBlank(uniqueToken)) {
            returnErrorResponse(response, IMOOCJSONResult.errorMsg("请登录..."));
            return false;
        }
        if (!uniqueToken.equals(headerUserToken)) {
            returnErrorResponse(response, IMOOCJSONResult.errorMsg("账号在异地登录..."));
            return false;
        }
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response,
                                    IMOOCJSONResult result) {
        OutputStream outputStream = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            outputStream = response.getOutputStream();
            outputStream.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //在访问controller后 渲染视图之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    //在访问controller后 渲染视图之后
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
