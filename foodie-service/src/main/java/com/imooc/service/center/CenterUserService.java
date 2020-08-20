package com.imooc.service.center;

import com.imooc.pojo.User;
import com.imooc.pojo.bo.center.CenterUserBO;

public interface CenterUserService {

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    public User queryUserInfo(String userId);

    /**
     * 修改用户信息
     * @param userId
     * @param centerUserBO
     */
    public User updateUserInfo(String userId, CenterUserBO centerUserBO);
}
