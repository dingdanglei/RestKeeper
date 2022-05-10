package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.restkeeper.store.entity.DishCategory;

import java.util.List;
import java.util.Map;

/**
 * @author dinglei
 * @date 2022/5/4 17:11
 */
public interface IDishCategoryService {

    /**
     * 添加分类
     * @param name 名称
     * @param type 类型
     * @return 添加是否成功
     */
    boolean add(String name , int type );

    /**
     * 修改分类
     * @param id 分类id
     * @param name 分类名称
     * @return 是否修改成功
     */
    boolean update(String id , String name);


    /**
     * 分类分页查询
     * @param pageNo 当前页
     * @param pageSize 分页大小
     * @return 分类分页信息
     */
    IPage<DishCategory> queryPage(int pageNo , int pageSize);


    /**
     * 分类列表查询
     * @param type 类型
     * @return 分类列表
     */
    List<Map<String,Object>> findCategoryList(Integer type);
}
