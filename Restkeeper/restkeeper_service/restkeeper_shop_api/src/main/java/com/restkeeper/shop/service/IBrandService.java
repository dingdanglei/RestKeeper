package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.shop.entity.Brand;

import java.util.List;
import java.util.Map;

public interface IBrandService extends IService<Brand> {

    /**
     * 品牌管理(分页查询)
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<Brand> queryPage(int pageNo, int pageSize);

    /**
     * 获取品牌列表
     * @return
     */
    List<Map<String,Object>> getBrandList();

}
