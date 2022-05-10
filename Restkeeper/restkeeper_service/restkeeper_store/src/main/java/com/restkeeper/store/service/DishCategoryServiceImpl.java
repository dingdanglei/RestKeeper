package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.exception.BussinessException;
import com.restkeeper.store.entity.DishCategory;
import com.restkeeper.store.mapper.DishCategoryMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author dinglei
 * @date 2022/5/4 17:22
 */
@Component
@Service(version = "1.0.0",protocol = "dubbo")
public class DishCategoryServiceImpl extends ServiceImpl<DishCategoryMapper, DishCategory> implements IDishCategoryService{

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(String name, int type) {
        checkNameExists(name);
        DishCategory dishCategory = new DishCategory();
        dishCategory.setName(name);
        dishCategory.setTOrder(0);
        dishCategory.setType(type);
        return this.save(dishCategory);
    }

    @Override
    public boolean update(String id, String categoryName) {
        checkNameExists(categoryName);
        UpdateWrapper<DishCategory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(DishCategory::getName,categoryName).eq(DishCategory::getCategoryId,id);
        return this.update(updateWrapper);
    }

    @Override
    public IPage<DishCategory> queryPage(int pageNo, int pageSize) {
        IPage<DishCategory> page = new Page<>(pageNo , pageSize);
        QueryWrapper<DishCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(DishCategory::getLastUpdateTime);
        return this.page(page,queryWrapper);
    }

    @Override
    public List<Map<String, Object>> findCategoryList(Integer type) {
        QueryWrapper<DishCategory> queryWrapper = new QueryWrapper<>();
        if(type !=null){
            queryWrapper.lambda().eq(DishCategory::getType,type);
        }
        queryWrapper.lambda().select(DishCategory::getCategoryId,DishCategory::getName);
        return this.listMaps(queryWrapper);
    }

    /**
     * 检查分类是否存在
     * @param name 分类名称
     */
    private void checkNameExists(String name) {
        QueryWrapper<DishCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(DishCategory::getCategoryId).eq(DishCategory::getName,name);
        Integer count = this.getBaseMapper().selectCount(queryWrapper);
        if(count > 0){
            throw new BussinessException("该分类名称已存在");
        }
    }






}
