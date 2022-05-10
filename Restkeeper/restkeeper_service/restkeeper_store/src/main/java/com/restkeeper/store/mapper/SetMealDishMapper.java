package com.restkeeper.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restkeeper.store.entity.SetMealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper extends BaseMapper<SetMealDish>{

    /**
     * 根据套餐id 查询菜品信息
     * @param setMailId 套餐id
     * @return 菜品信息
     */
    @Select(" select * from  t_setmeal_dish where setmeal_id = #{setmailid} ")
    List<SetMealDish> selectDishes(@Param("setMealId") String setMailId);
}
