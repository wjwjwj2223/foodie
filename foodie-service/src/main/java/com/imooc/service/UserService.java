package com.imooc.service;

import com.imooc.pojo.User;
import com.imooc.pojo.bo.UserBO;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wangjie
 */
public interface UserService {

    /*
    * 判断用户名是否存在
    */
    boolean queryUsernameIsExist(String username);

    /*
    * 创建用户
    */
    User createUser(UserBO userBO);

    /*
     * 检索用户名个密码是否匹配  用于登录
     */
    public User queryUserForLogin(String username, String password);
}
