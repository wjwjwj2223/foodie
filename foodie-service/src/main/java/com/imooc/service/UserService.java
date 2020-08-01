package com.imooc.service;

import com.imooc.pojo.User;
import com.imooc.pojo.bo.UserBO;

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
}
