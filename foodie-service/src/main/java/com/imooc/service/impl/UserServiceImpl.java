package com.imooc.service.impl;

import com.imooc.mapper.UserMapper;
import com.imooc.pojo.User;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * @author wangjie
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryUsernameIsExist(String username) {
        Example userExample = new Example(User.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        User user = userMapper.selectOneByExample(userExample);
        return user != null;
    }

    @Override
    public User createUser(UserBO userBO) {
        return null;
    }
}
