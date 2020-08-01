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
    public boolean queryUsernameIsExist(String username);

    public User createUser(UserBO userBO);
}
