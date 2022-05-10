package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.shop.dto.StoreDTO;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.utils.Result;

import java.util.List;

public interface IStoreService extends IService<Store> {
    /**
     * 根据名称分页查询
     * @param pageNo 当前页
     * @param pageSize 每页大小
     * @param name 姓名
     * @return 门店信息
     */
    IPage<Store> queryPageByName(int pageNo , int pageSize , String name );

    /**
     * 获取所有有效的省份列表
     * @return 省份列表
     */
    List<String> getAllProvince();

    /**
     * 根据省份获取门店列表
     * @param province 省份
     * @return 门店列表
     */
    List<StoreDTO> getStoreByProvince(String province);

    /**
     * 获取管理员管理的门店列表
     * @return 门店列表
     */
    List<StoreDTO> getStoresByManagerId();

    /**
     * 门店切换
     * @param storeId 门店id
     * @return 门店信息
     */
    Result switchStore(String storeId);

}
