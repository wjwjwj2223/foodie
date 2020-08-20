package com.imooc.service.center;

import com.imooc.pojo.User;

public interface CenterUserService {

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    public User queryUserInfo(String userId);

}
