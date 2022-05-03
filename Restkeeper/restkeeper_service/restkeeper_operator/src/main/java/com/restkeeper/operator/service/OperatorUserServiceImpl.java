package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.restkeeper.operator.entity.OperatorUser;
import com.restkeeper.operator.mapper.OperatorUserMapper;
import com.restkeeper.utils.JWTUtil;
import com.restkeeper.utils.MD5CryptUtil;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.HashMap;

//@Service("operatorUserService")
@Service(version = "1.0.0",protocol = "dubbo")
/**
 * dubbo中支持的协议
 * dubbo 默认
 * rmi
 * hessian
 * http
 * webservice
 * thrift
 * memcached
 * redis
 */
@RefreshScope
public class OperatorUserServiceImpl extends ServiceImpl<OperatorUserMapper, OperatorUser> implements IOperatorUserService{

    @Value("${gateway.secret}")
    private String secret ;

    /**
     * 管理员登录
     * @param loginName  登录名
     * @param password   密码
     * @return
     */
    @Override
    public Result login(String loginName, String password) {
        Result result = new Result() ;
        //参数校验
        if(StringUtils.isEmpty(loginName)){
            result.setStatus(ResultCode.error);
            result.setDesc("用户名为空");
            return result ;
        }

        if(StringUtils.isEmpty(password)){
            result.setStatus(ResultCode.error);
            result.setDesc("密码为空");
            return result ;
        }
        // 查询用户是否存在
        QueryWrapper<OperatorUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loginname" , loginName);
        OperatorUser user = this.getOne(queryWrapper);
        if( user ==null ){
            result.setStatus(ResultCode.error);
            result.setDesc("用户不存在");
            return result;
        }
        // 比对密码
        String salts = MD5CryptUtil.getSalts(user.getLoginpass());
        if(!Md5Crypt.md5Crypt(password.getBytes(),salts).equals(user.getLoginpass())){
            result.setStatus(ResultCode.error);
            result.setDesc("密码不正确");
            return result;
        }
        // 生成jwt令牌
        HashMap<String, Object> tokenInfoMap = Maps.newHashMap();
        tokenInfoMap.put("loginName",user.getLoginname());
        String token = null;
        try {
            token = JWTUtil.createJWTByObj(tokenInfoMap, secret);
        }catch (Exception e){
            e.printStackTrace();
            result.setStatus(ResultCode.error);
            result.setDesc("生成令牌失败");
            return result;
        }
        //返回结果
        result.setStatus(ResultCode.success);
        result.setDesc("ok");
        result.setData(user);
        result.setToken(token);
        return result;
    }

    @Override
    public IPage<OperatorUser> queryPagebyName(int pageNum, int pageSize, String name) {
        IPage<OperatorUser> page = new Page<>(pageNum, pageSize);
        QueryWrapper<OperatorUser> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)){
            queryWrapper.like("loginname" , name);
        }
        return this.page(page,queryWrapper);
    }
}
