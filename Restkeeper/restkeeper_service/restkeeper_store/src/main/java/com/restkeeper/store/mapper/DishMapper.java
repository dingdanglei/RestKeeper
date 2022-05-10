package com.restkeeper.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restkeeper.redis.MybatisRedisCache;
import com.restkeeper.store.entity.Dish;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface DishMapper extends BaseMapper<Dish>{

    /**
     * 根据分类查询菜品集合
     * @param dishCategoryId 分类id
     * @return 菜品集合
     */
    @Select(" select * from t_dish where category_id = #{dishCategoryId} and is_deleted=0 order by last_update_time desc")
    public List<Dish> selectDishByCategory(@Param("dishCategoryId") String dishCategoryId);
	
}
