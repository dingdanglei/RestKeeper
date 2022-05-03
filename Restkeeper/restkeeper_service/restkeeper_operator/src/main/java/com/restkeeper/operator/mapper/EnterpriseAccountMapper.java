package com.restkeeper.operator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restkeeper.operator.entity.EnterpriseAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author dinglei
 * @date 2022/5/2 18:58
 */
@Mapper
public interface EnterpriseAccountMapper extends BaseMapper<EnterpriseAccount> {

    /**
     * 账号删除后还原
     * @param id
     * @return
     */
    @Update("update t_enterprise_account set is_deleted = 0 where is_deleted =1 and enterprise_id = #{id} ")
    boolean recovery(@Param("id") String id) ;
}
