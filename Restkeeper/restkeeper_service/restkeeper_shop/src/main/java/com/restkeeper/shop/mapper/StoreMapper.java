package com.restkeeper.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restkeeper.shop.entity.Store;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StoreMapper extends BaseMapper<Store> {


    /**
     *  查询品牌关联的门店总数
     * @param brandId
     * @return
     */
    @Select(" select count(1) from t_store where brand_id = #{brandId} and status = 1 and is_deleted = 0 ")
    Integer getStoreCount(@Param("brandId") String brandId) ;

    /**
     * 查询品牌关联的城市总数
     * @param brandId
     * @return
     */
    @Select(" select count(distinct(city) from t_store where brandId =#{brandId} and status=1 and is_deleted=0)")
    Integer getCityCount(@Param("brandId") String brandId);

    /**
     * 根据门店管理员id查询关联的门店列表
     * @param managerId
     * @return
     */
    @Select(" select * from t_store where manageId = #{managerId} order by last_update_time desc ")
    List<Store> selectStoreInfoByManagerId(@Param("managerId") String managerId);

    /**
     * 查询所有有效的省份
     * @return
     */
    @Select(" select distinct(province) from t_store where status = 1 and is_deleted = 0 ")
    public List<String> getAllProvince();


}
