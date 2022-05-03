package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.shop.dto.StoreDTO;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.mapper.StoreMapper;
import com.restkeeper.utils.BeanListUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Service(version = "1.0.0",protocol = "dubbo")
@Slf4j
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements IStoreService {
    @Override
    public IPage<Store> queryPageByName(int pageNo, int pageSize, String name) {
        IPage<Store> page = new Page<>(pageNo , pageSize);
        QueryWrapper<Store> queryWrapper = new QueryWrapper();
        if(StringUtils.isNotEmpty(name)){
            queryWrapper.lambda().like(Store::getStoreName,name);
        }
        return this.page(page,queryWrapper);
    }

    /**
     * 查询所有省份信息
     * @return
     */
    @Override
    public List<String> getAllProvince() {
        return getBaseMapper().getAllProvince();
    }

    /**
     * 根据省份查询门店列表
     * @param province
     * @return
     */
    @Override
    public List<StoreDTO> getStoreByProvince(String province) {
        QueryWrapper<Store> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Store::getStatus,1);
        if(StringUtils.isNotEmpty(province) && !"all".equalsIgnoreCase(province)){
            queryWrapper.lambda().eq(Store::getProvince,province);
        }
        List<Store> list = this.list(queryWrapper);
        List<StoreDTO>  storeDTOList ;
        try{
            return storeDTOList = BeanListUtils.copy(list, StoreDTO.class);
        }catch(Exception e){
            log.info("转换失败");
        }
        return new ArrayList<StoreDTO>();
    }
}
