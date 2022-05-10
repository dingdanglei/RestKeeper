package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.shop.entity.StoreManager;
import com.restkeeper.utils.Result;

import java.util.List;

public interface IStoreManagerService extends IService<StoreManager> {

    /**
     * 根据条件分页查询(手机号/姓名)
     * @param pageNo 当前页
     * @param pageSize 每页数据大小
     * @param criteria 条件(手机号/姓名)
     * @return 分页数据
     */
    IPage<StoreManager> queryPageByCriteria(int pageNo, int pageSize , String criteria);

    /**
     * 添加门店管理员
     * @param name 姓名
     * @param phone 手机号
     * @param email 邮箱
     * @param storeIds 关联的门店id
     * @return 添加是否成功
     */
    boolean addStoreManager(String name , String phone ,String email, List<String> storeIds  );

    /**
     * 修改门店管理员
     * @param storeManagerId 门店管理员id
     * @param name 管理员名称
     * @param phone 手机号
     * @param email 邮箱
     * @param storeIds 关联的门店ID
     * @return 是否修改成功
     */
    boolean updateStoreManager(String storeManagerId , String name , String phone , String email , List<String> storeIds) ;

    /**
     * 暂停门店管理员
     * @param storeManagerId 门店管理员id
     * @return 暂停是否成功
     */
    boolean pauseStoreManager(String storeManagerId);

    /**
     * 删除门店管理员
     * @param storeManagerId 门店管理员id
     * @return 删除是否成功
     */
    boolean deleteStoreManager(String storeManagerId);

    /**
     * 门店管理员登录
     * @param shopId 商户id
     * @param phone 登录号(手机号登录)
     * @param loginPass 密码
     * @return 封装后的result
     */
    Result login(String shopId , String phone , String loginPass);


}
