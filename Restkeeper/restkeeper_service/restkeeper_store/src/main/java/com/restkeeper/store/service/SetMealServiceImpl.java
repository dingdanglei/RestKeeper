package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.store.entity.SetMeal;
import com.restkeeper.store.entity.SetMealDish;
import com.restkeeper.store.mapper.SetMealMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dinglei
 * @date 2022/5/4 21:10
 */
@org.springframework.stereotype.Service("setMealService")
@Service(version = "1.0.0",protocol = "dubbo")
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements ISetMealService{

    @Autowired
    @Qualifier("setMealDishService")
    private ISetMealDishService setMealDishService;

    @Override
    public IPage<SetMeal> queryPage(int pageNo, int pageSize, String name) {
        IPage<SetMeal> page = new Page<>();
        QueryWrapper<SetMeal> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(name)){
            queryWrapper.lambda().like(SetMeal::getName,name);
        }
        return this.page(page,queryWrapper);
    }

    @Override
    @Transactional
    public boolean add(SetMeal setMeal, List<SetMealDish> setMealDishes) {
        this.save(setMeal);
        setMealDishes.forEach(setMealDish -> {
            setMealDish.setSetMealId(setMeal.getId());
            setMealDish.setIndex(0);
        });
        return setMealDishService.saveBatch(setMealDishes);
    }

    @Override
    public boolean update(SetMeal setMeal, List<SetMealDish> setMealDishes) {
        try {
            //修改套餐基础信息
            this.updateById(setMeal);
            //删除原有的菜品关联关系
            if (setMealDishes != null || setMealDishes.size()>0){
                QueryWrapper<SetMealDish> queryWrapper =new QueryWrapper<>();
                queryWrapper.lambda().eq(SetMealDish::getSetMealId,setMeal.getId());
                setMealDishService.remove(queryWrapper);
                //重建菜品的关联关系
                setMealDishes.forEach((setMealDish)->{
                    setMealDish.setSetMealId(setMeal.getId());
                });
                setMealDishService.saveBatch(setMealDishes);
            }
            return true ;
        }catch (Exception e){
            e.printStackTrace();
            return false ;
        }

    }

}
