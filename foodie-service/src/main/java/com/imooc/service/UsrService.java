package com.imooc.service;

import com.imooc.pojo.Stu;

/**
 * @author wangjie
 */
public interface UsrService {

    /*
    * 判断用户名是否存在
    */
    public boolean queryUsernameIsExist(String username);

}
