package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.shop.entity.StoreManager;

import java.util.List;

public interface IStoreManagerService extends IService<StoreManager> {

    /**
     * 根据条件分页查询(手机号/姓名)
     * @param pageNo
     * @param pageSize
     * @param criteria
     */
    IPage<StoreManager> queryPageByCriteria(int pageNo, int pageSize , String criteria);

    /**
     * 添加门店管理员
     * @param name 姓名
     * @param phone 手机号
     * @param storeIds 关联的门店id
     * @return
     */
    public boolean addStoreManager(String name , String phone ,String email, List<String> storeIds  );
}
