package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.Dish;
import com.restkeeper.store.entity.DishFlavor;

import java.util.List;
import java.util.Map;

/**
 * @author dinglei
 * @date 2022/5/4 20:14
 */
public interface IDishService extends IService<Dish> {

    /**
     * 新增菜品
     * @param dish 菜品信息
     * @param dishFlavorList 菜品关联的口味信息
     * @return 是否保存成功
     */
    boolean save(Dish dish  , List<DishFlavor> dishFlavorList);


    /**
     * 修改菜品
     * @param dish 菜品信息
     * @param flavorList 菜品关联的口味信息
     * @return 修改是否成功
     */
    boolean update(Dish dish, List<DishFlavor> flavorList);

    /**
     * 查询菜品列表信息
     * @param categoryId 分类id
     * @param name 菜品名称
     * @return 菜品列表
     */
    List<Map<String,Object>> findEnableDishListInfo(String categoryId , String name );
}
