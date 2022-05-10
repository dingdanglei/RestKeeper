package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.SetMeal;
import com.restkeeper.store.entity.SetMealDish;

import java.util.List;

/**
 * @author dinglei
 * @date 2022/5/4 21:06
 */
public interface ISetMealService extends IService<SetMeal> {

    /**
     * 套餐信息分页查询
     * @param pageNo 当前页
     * @param pageSize 分页大小
     * @param name 套餐名称
     * @return 套餐分页信息
     */
    IPage<SetMeal> queryPage(int pageNo ,int pageSize , String name );

    /**
     * 添加套餐
     * @param setMeal 套餐信息
     * @param setMealDishes 套餐和菜品关联信息
     * @return 是否添加成功
     */
    boolean add(SetMeal setMeal , List<SetMealDish> setMealDishes);

    /**
     * 更新套餐
     * @param setMeal 套餐信息
     * @param setMealDishes 套餐关联的菜品
     * @return 是否更新成功
     */
    boolean update(SetMeal setMeal , List<SetMealDish> setMealDishes);
}
