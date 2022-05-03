package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.utils.Result;

/**
 * @author dinglei
 * @date 2022/5/2 19:04
 */
public interface IEnterpriseAccountService extends IService<EnterpriseAccount> {

    /**
     * 根据企业名称查询
     * @param pageNum
     * @param pageSize
     * @param enterpriseName
     * @return
     */
    IPage<EnterpriseAccount> queryPageByName(int pageNum , int pageSize , String enterpriseName);

    /**
     * 新增账号
     * @param account
     * @return
     */
    boolean add(EnterpriseAccount account);

    /**
     * 账号还原
     * @param id
     * @return
     */
    boolean recovery(String id) ;


    /**
     * 密码重置
     * @param id
     * @param password
     * @return
     */
    boolean resetPwd(String id , String password ) ;

    /**
     * 根据商户ID、手机号、登录密码校验登录
     * @param shopId
     * @param telephone
     * @param loginPass
     * @return
     */
    Result login(String shopId , String telephone , String loginPass);
}
