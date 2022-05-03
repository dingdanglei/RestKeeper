package com.restkeeper.operator.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.email.EmailObject;
import com.restkeeper.operator.config.RabbitMQConfig;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.mapper.EnterpriseAccountMapper;
import com.restkeeper.utils.*;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 企业账号管理 服务实现类
 * @author dinglei
 * @date 2022/5/2 19:08
 */
@Service(version = "1.0.0",protocol = "dubbo")
@RefreshScope
public class EnterpriseAccountServiceImpl extends ServiceImpl<EnterpriseAccountMapper, EnterpriseAccount> implements IEnterpriseAccountService{



    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${email.operator.templateCode}")
    private String templateCode;

    @Value("${gateway.secret}")
    private String secret;

    /**
     * 消息发送
     */
    private void sendMessage(String email,String shopId,String pwd){
        EmailObject emailObject = new EmailObject();
        emailObject.setEmail(email);
        emailObject.setSubject("商户账号密码下发");
        templateCode = templateCode.replace("shopId",shopId);
        templateCode = templateCode.replace("password",pwd);
        /*Map<String, Object> params = new HashMap<>(3);
        params.put("shopId", shopId);
        params.put("password", pwd);
        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext parserContext = new TemplateParserContext();
        String content = parser.parseExpression(templateCode,parserContext).getValue(params, String.class);*/
        emailObject.setContent(templateCode);
        rabbitTemplate.convertAndSend(RabbitMQConfig.ACCOUNT_QUEUE, JSON.toJSONString(emailObject));
    }



    @Override
    public IPage<EnterpriseAccount> queryPageByName(int pageNum, int pageSize, String enterpriseName) {
        IPage<EnterpriseAccount> page = new Page<>(pageNum , pageSize);
        QueryWrapper<EnterpriseAccount> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(enterpriseName)){
            queryWrapper.like("enterprise_name" , enterpriseName);
        }
        return this.page(page , queryWrapper);
    }

    @Override
    @Transactional
    public boolean add(EnterpriseAccount account) {
        boolean flag = true ;
        try {
            // 账号，密码特殊处理
            String shopId = getShopId();
            account.setShopId(shopId);
            //密码随机6位
            String password  = RandomStringUtils.randomNumeric(6);
            account.setPassword(Md5Crypt.md5Crypt(password.getBytes()));
            //发送消息
            sendMessage(account.getEnterpriseEmail(),shopId,password);
            this.save(account);
        }catch(Exception e){
            flag = false ;
            throw e ;
        }
        return flag;
    }

    @Override
    @Transactional
    public boolean recovery(String id) {
        return this.getBaseMapper().recovery(id);
    }

    @Override
    @Transactional
    public boolean resetPwd(String id, String password) {
        boolean flag=true;
        try {
            EnterpriseAccount account = this.getById(id);
            if(account == null ){
                return false ;
            }
            String newPwd;
            if(StringUtils.isNotEmpty(password)){
                newPwd= password ;
            }else{
                newPwd = RandomStringUtils.randomNumeric(6);
            }
            account.setPassword(Md5Crypt.md5Crypt(newPwd.getBytes()));
            this.updateById(account);
            //发送消息
            sendMessage(account.getEnterpriseEmail(),account.getShopId(),newPwd);
        }catch(Exception e){
            flag= false ;
            throw e ;
        }
        return flag;
    }

    @Override
    public Result login(String shopId, String telephone, String loginPass) {
        Result result = new Result();
        //参数校验
        if(StringUtils.isEmpty(shopId)){
            result.setStatus(ResultCode.error);
            result.setDesc("商户id为空");
            return result ;
        }
        if(StringUtils.isEmpty(loginPass)){
            result.setStatus(ResultCode.error);
            result.setDesc("密码为空");
            return result ;
        }
        // 查询用户信息
        QueryWrapper<EnterpriseAccount> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(EnterpriseAccount::getPhone,telephone);
        queryWrapper.lambda().eq(EnterpriseAccount::getShopId,shopId);
        queryWrapper.lambda().notIn(EnterpriseAccount::getStatus, AccountStatus.Forbidden.getStatus());
        EnterpriseAccount account = this.getOne(queryWrapper);
        if(account == null ){
            result.setStatus(ResultCode.error);
            result.setDesc("账号不存在");
            return result ;
        }
        // 校验密码
        String salts = MD5CryptUtil.getSalts(account.getPassword());
        if(!Md5Crypt.md5Crypt(loginPass.getBytes(),salts).equals(account.getPassword())){
            result.setStatus(ResultCode.error);
            result.setDesc("密码不正确");
            return result ;
        }
        // 生成令牌
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("shopId",account.getShopId());
        map.put("loginName",account.getEnterpriseName());
        map.put("userType", SystemCode.USER_TYPE_SHOP);
        String token = null ;
        try {
            token = JWTUtil.createJWTByObj(map , secret) ;
        }catch(Exception e){
            log.error("加密失败"+e.getMessage());
            result.setStatus(ResultCode.error);
            result.setDesc("加密失败");
            return result ;
        }
        result.setStatus(ResultCode.success);
        result.setDesc("ok");
        result.setData(account);
        result.setToken(token);
        return result;
    }

    private String getShopId() {
        String shopId = RandomStringUtils.randomNumeric(8);
        QueryWrapper<EnterpriseAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id" , shopId);
        EnterpriseAccount account = this.getOne(queryWrapper);
        if(account!=null){
            this.getShopId();
        }
        return shopId;
    }

}
