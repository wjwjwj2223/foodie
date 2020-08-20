package com.imooc.service.impl.center;

import com.imooc.mapper.UserMapper;
import com.imooc.pojo.User;
import com.imooc.service.center.CenterUserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CenterUserServiceImpl implements CenterUserService {

    @Autowired
    public UserMapper usersMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public User queryUserInfo(String userId) {
        User user = usersMapper.selectByPrimaryKey(userId);
        user.setPassword(null);
        return user;
    }

}