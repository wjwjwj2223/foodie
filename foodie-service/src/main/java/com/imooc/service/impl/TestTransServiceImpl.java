package com.imooc.service.impl;

import com.imooc.service.StuService;
import com.imooc.service.TestTransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestTransServiceImpl implements TestTransService {

    @Autowired
    private StuService stuService;

    //事务传播
//    REQUIRED(0), 使用当前的事务，如果当前没有事务，则自己新建一个事务，子方法是必须运行在一个事务中的；
//    如果当前存在事务，则加入这个事务，成为一个整体。
//    举例: 领导没饭吃，我有钱，我会自己买了自己吃， 领导有的吃， 会分给你一起吃
//    SUPPORTS(1), 如果当前有事务，则使用事务。如果当前没有事务，则不使用事务.
//    举例: 领导没饭吃， 我也没饭吃。 领导有饭吃，我也有饭吃。
//    MANDATORY(2), 该传播属性强制必须存在一个事务，如果不存在，则抛出异常。
//    举例: 领导必须管饭，不管饭没饭吃，我就不乐意了，就不干了（抛出异常）.
//    REQUIRES_NEW(3), 如果当前有事务，则挂起该事务，并且自己创建一个新的事务给自己使用；
//    如果当前没有事务则同REQUIRED
//    NOT_SUPPORTED(4), 如果当前有事务，则把事务挂起，自己不使用事务去运行数据库操作
//    NEVER(5),  如果当前有事务存在，则抛出异常。
//    NESTED(6), 如果当前有事务，则开启子事务（嵌套事务）。嵌套事务是独立提交或者回滚；
//    如果当前没有事务 则同 REQUIRED.
    //但是如果主事务提交，则会携带子事务一起提交。
    //如果主事务回滚，则子事务会一起回滚。相反，如果子事务异常，则父事务可以回滚或者不回滚。

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void testPropagationTrans() {

        stuService.saveParent();

        stuService.saveChildren();
    }

}
