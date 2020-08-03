package com.imooc.service.impl;

import com.imooc.mapper.CategorysMapperCustom;
import com.imooc.mapper.CategorysMapper;
import com.imooc.pojo.Categorys;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.service.CategorysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CategorysServiceImpl implements CategorysService {

    @Autowired
    private CategorysMapper categoryMapper;

    @Autowired
    private CategorysMapperCustom categorysMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Categorys> queryAllRootLevelCat() {
        Example example = new Example(Categorys.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", 1);
        List<Categorys> categories = categoryMapper.selectByExample(example);
        return categories;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVO> getSubCatList(Integer rootCatId) {
        return categorysMapperCustom.getSubCatList(rootCatId);
    }
}
