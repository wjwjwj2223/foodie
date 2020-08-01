package com.imooc.service.impl;

import com.imooc.mapper.StuMapper;
import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StuServiceImpl implements StuService {

    @Autowired
    private StuMapper stuMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Stu getStuInfo(int id) {
        return stuMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveStu() {
        Stu stu = new Stu();
        stu.setId(2);
        stu.setAge(20);
        stu.setName("哈哈");
        stuMapper.insert(stu);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteStu(int id) {
        stuMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateStu(int id) {
        Stu stu = new Stu();
        stu.setId(id);
        stu.setAge(21);
        stu.setName("呵呵");
        stuMapper.updateByPrimaryKey(stu);
    }

    @Override
    public void saveParent() {
        Stu stu = new Stu();
        stu.setId(1000);
        stu.setAge(21);
        stu.setName("parent");
        stuMapper.insert(stu);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveChildren() {
        saveChildren1();
        int a = 1 / 0;
        saveChildren2();
    }

    private void saveChildren1() {
        Stu stu = new Stu();
        stu.setId(1001);
        stu.setAge(11);
        stu.setName("children 1");
        stuMapper.insert(stu);
    }

    private void saveChildren2() {
        Stu stu = new Stu();
        stu.setId(1002);
        stu.setAge(12);
        stu.setName("children 2");
        stuMapper.insert(stu);
    }

}
