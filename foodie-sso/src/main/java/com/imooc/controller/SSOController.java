package com.imooc.controller;

import com.imooc.pojo.User;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class SSOController {

    @Autowired
    private UserService userService;

    @Autowired
    protected RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_TICKET = "redis_user_ticket";
    public static final String REDIS_TMP_TICKET = "redis_tmp_ticket";
    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";

    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        model.addAttribute("returnUrl", returnUrl);
        //1.获取userticket 如果能够获取到 证明用户登录过 此时签发一个临时票据
        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        boolean isVerify = verifyUserTicket(userTicket);
        if (isVerify) {
            String tmpTicket = createTmpTicket();
            return "redirect:" + returnUrl + "?tempTicket=" + tmpTicket;
        }
        //2.用户从未登录  第一只登录跳转到cas 统一登录页面
        return "login";
    }

     /* CAS的统一登录接口
     * 目的
     * 1.登录后创建用户的全局会话 -> uniqueToken
     * 2.创建用户全局门票, 用以表示在cas端是否登录 -> userTicket
     * 3.创建用户的临时票据, 用于回跳回传 -> tempTicket
     * */
    @PostMapping("/doLogin")
    public String doLogin(String username,
                          String password,
                          String returnUrl,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        model.addAttribute("returnUrl", returnUrl);
        //0 判断用户名和密码不能为空
        if (StringUtils.isBlank(username)
                || StringUtils.isBlank(password)) {
            model.addAttribute("errmsg","用户名或者密码不能为空");
            return "login";
        }
        //1. 实现登录
        User userResult = userService.queryUserForLogin(username,
                MD5Utils.getMD5Str(password));
        if (userResult == null) {
            model.addAttribute("errmsg","用户名或者密码不正确");
            return "login";
        }

        //2. 实现用户的redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN
                        + ":"
                        + userResult.getId(),
                JsonUtils.objectToJson(usersVO));

        //3. 生成ticket门票 全局门票 代表用户在cas端登录过
        String userTicket = UUID.randomUUID().toString().trim();

        //3.1 用户全局门票需要放入cas端的cookie中
        setCookie(COOKIE_USER_TICKET, userTicket, response);

        //4. userTicket 关联用户id 并且放入到redis中
        // 代表这个用户有门票了可以在各个景区游玩
        redisOperator.set(REDIS_USER_TICKET
                + ":" +
                userTicket,
                userResult.getId());

        //5. 生成临时票据  回跳到调用端网站 是由cas签发的一次性的临时的ticket
        String tempTicket = createTmpTicket();

        /*
        userTicket: 用于表示用户在cas端的一个登陆状态
        tempTicket: 用于颁发给用户的一次性的验证的票据, 有时效性
        * */

        /*
        我们去动物园玩耍, 在大门口买了一张统一的门票，这个就是cas系统的全局门票和
        用户全局会话, 动物园里面的一些小的景点，需要使用统一门票换取临时门票,
        有了临时
        门票就可以去一个个小的景点，当我们使用完临时票据以后  需要销毁
        * */

        return "redirect:" + returnUrl + "?tempTicket=" + tempTicket;
    }

    @GetMapping("/verifyTmpTicket")
    @ResponseBody
    public IMOOCJSONResult verifyTmpTicket(String tmpTicket,
                                           HttpServletRequest request,
                                           HttpServletResponse response)
            throws Exception {
        //使用一次性临时票据 来验证用户是否登录 如果登录过 把用户的会话信息返回
        // 给站点
        //使用完毕后 需要销毁临时票据
        String tmpTicketValue = redisOperator
                .get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketValue)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }
        //0. 如果 临时票据ok 则需要销毁 并且拿到cas端cookie中全局的userticket
        // 以此在
        // 获取用户信息
        if (!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        } else {
            //销毁临时票据
            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);
        }
        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        String userId = redisOperator
                .get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }

        //2.验证门票对应的user会话是否存在
        String userRedis = redisOperator
                .get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return IMOOCJSONResult.errorUserTicket("用户票据异常");
        }
        //验证成功
        return IMOOCJSONResult
                .ok(JsonUtils.jsonToPojo(userRedis, UsersVO.class));
    }

    @GetMapping("/logout")
    @ResponseBody
    public IMOOCJSONResult logout(String userId,
                                           HttpServletRequest request,
                                           HttpServletResponse response)
            throws Exception {
        // 0. 获取cas中的用户门票
        String userTicket = getCookie(request, COOKIE_USER_TICKET);
        // 1. 清除userTicket 票据 redis/cookie
        deleteCookie(COOKIE_USER_TICKET, response);
        redisOperator.del(REDIS_USER_TICKET + ":" + userTicket);
        // 2.清除用户全局会话
        redisOperator.del(REDIS_USER_TOKEN+":"+userId);
        return IMOOCJSONResult.ok();
    }

    /*
    * 创建临时票据
    * */
    private String createTmpTicket() {
        String tempTicket = UUID.randomUUID().toString().trim();
        try {
            redisOperator.set(REDIS_TMP_TICKET
                    + ":"
                    + tempTicket,
                    MD5Utils.getMD5Str(tempTicket),
                    600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempTicket;
    }

    private void setCookie(String key,
                           String value,
                           HttpServletResponse response) {
        Cookie cookie = new Cookie(key,value);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getCookie(HttpServletRequest request,
                             String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || StringUtils.isBlank(key)) {
            return null;
        }
        String cookieValue = null;
        for (int i = 0; i< cookies.length; i++) {
            if (cookies[i].getName().equals(key)) {
                cookieValue = cookies[i].getValue();
                return cookieValue;
            }
        }
        return null;
    }


    private void deleteCookie(String key,
                           HttpServletResponse response) {
        Cookie cookie = new Cookie(key,null);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    /*检验cas用户门票
     * */
    private boolean verifyUserTicket(String userTicket) {
        //门票不能为空
        if (StringUtils.isBlank(userTicket)){
            return false;
        }
        //验证cas门票是否有效
        String userId = redisOperator.get(REDIS_USER_TICKET
                + ":" + userTicket);
        if (StringUtils.isBlank(userId)){
            return false;
        }
        //验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN
                + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return false;
        }
        return true;
    }
}
