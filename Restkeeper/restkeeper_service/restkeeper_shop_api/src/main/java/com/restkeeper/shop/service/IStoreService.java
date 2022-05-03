package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.shop.dto.StoreDTO;
import com.restkeeper.shop.entity.Store;

import java.util.List;

public interface IStoreService extends IService<Store> {
    /**
     * 根据名称分页查询
     * @param pageNo
     * @param pageSize
     * @param name
     * @return
     */
    IPage<Store> queryPageByName(int pageNo , int pageSize , String name );

    /**
     * 获取所有有效的省份列表
     * @return
     */
    List<String> getAllProvince();

    /**
     * 根据省份获取门店列表
     * @param province
     * @return
     */
    public List<StoreDTO> getStoreByProvince(String province);

}
