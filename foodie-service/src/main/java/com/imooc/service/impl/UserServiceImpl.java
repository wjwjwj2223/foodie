package com.imooc.service.impl;

import com.imooc.mapper.UsrMapper;
import com.imooc.pojo.Usr;
import com.imooc.service.UsrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * @author wangjie
 */
@Service
public class UserServiceImpl implements UsrService {

    @Autowired
    private UsrMapper usrMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryUsernameIsExist(String username) {
        Example userExample = new Example(Usr.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        Usr usr = usrMapper.selectOneByExample(userExample);
        return usr != null;
    }
}
