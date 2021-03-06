package com.restkeeper.shop.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.email.EmailObject;
import com.restkeeper.shop.config.RabbitMQConfig;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.entity.StoreManager;
import com.restkeeper.shop.mapper.StoreManagerMapper;
import com.restkeeper.utils.JWTUtil;
import com.restkeeper.utils.MD5CryptUtil;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

@Service(version = "1.0.0",protocol = "dubbo")
public class StoreManagerServiceImpl extends ServiceImpl<StoreManagerMapper, StoreManager> implements IStoreManagerService {
    @Autowired
    @Qualifier("storeService")
    private IStoreService storeService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${email.operator.templateCode}")
    private String templateCode;

    @Value("${gateway.secret}")
    private String secret;

    @Override
    public IPage<StoreManager> queryPageByCriteria(int pageNo, int pageSize, String criteria) {
        QueryWrapper<StoreManager> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(criteria)){
            queryWrapper.lambda().eq(StoreManager::getStoreManagerPhone,criteria).or().eq(StoreManager::getStoreManagerName,criteria);
        }
        IPage<StoreManager> page = new Page<>(pageNo,pageSize);
        return this.page(page,queryWrapper);
    }

    @Override
    public boolean addStoreManager(String name, String phone,String email ,  List<String> storeIds) {
        boolean flag = true ;
        try {
            StoreManager manager = new StoreManager();
            manager.setStoreManagerName(name);
            manager.setStoreManagerEmail(email);
            manager.setStoreManagerPhone(phone);
            String pwd = RandomStringUtils.randomNumeric(8);
            manager.setPassword(Md5Crypt.md5Crypt(pwd.getBytes()));
            this.save(manager);
            //??????store????????????????????????????????????
            String storeManagerId = manager.getStoreManagerId();
            UpdateWrapper<Store> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().in(Store::getStoreId,storeIds).set(Store::getStoreManagerId,storeManagerId);
            flag = storeService.update(updateWrapper);
            if(flag){
                // ?????????????????????????????????????????????
                // ????????????
                sendEmail( manager.getShopId(),pwd,email);
            }
        }catch (Exception e){
            flag = false ;
            throw e ;
        }
        return flag;
    }

    @Override
    public boolean updateStoreManager(String storeManagerId, String name, String phone, String email, List<String> storeIds) {
        boolean flag = true ;
        try {
            StoreManager manager = this.getById(storeManagerId);
            if(StringUtils.isNotEmpty(name)){
                manager.setStoreManagerName(name);
            }
            if(StringUtils.isNotEmpty(phone)){
                manager.setStoreManagerPhone(phone);
            }
            if(StringUtils.isNotEmpty(email)){
                manager.setStoreManagerEmail(email);
            }
            this.updateById(manager);
            // ?????????????????????????????????????????????
            UpdateWrapper<Store> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(Store::getStoreManagerId,null).eq(Store::getStoreManagerId,storeManagerId);
            storeService.update(updateWrapper);
            // ???????????????????????????????????????
            UpdateWrapper<Store> updateWrapperNew = new UpdateWrapper<>();
            updateWrapperNew.lambda().in(Store::getStoreId,storeIds).set(Store::getStoreManagerId,storeManagerId);
            storeService.update(updateWrapperNew);
        }catch (Exception e) {
            flag = false;
            throw e;
        }
        return flag;
    }

    @Override
    public boolean pauseStoreManager(String storeManagerId) {
        UpdateWrapper<StoreManager> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(StoreManager::getStatus, SystemCode.FORBIDDEN).eq(StoreManager::getStoreManagerId,storeManagerId);
        return this.update(updateWrapper);
    }

    @Override
    public boolean deleteStoreManager(String storeManagerId) {
        //????????????
        this.removeById(storeManagerId);
        // ???????????????????????????????????????
        UpdateWrapper<Store> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(Store::getStoreManagerId,null).eq(Store::getStoreManagerId,storeManagerId);
        return storeService.update(updateWrapper);
    }

    @Override
    public Result login(String shopId, String phone, String loginPass) {
        Result result = new Result();
        //????????????
        if(StringUtils.isEmpty(shopId)){
            result.setStatus(ResultCode.error);
            result.setDesc("???????????????");
            return result ;
        }
        if(StringUtils.isEmpty(phone)){
            result.setStatus(ResultCode.error);
            result.setDesc("???????????????");
            return result ;
        }
        if(StringUtils.isEmpty(loginPass)){
            result.setStatus(ResultCode.error);
            result.setDesc("????????????");
            return result ;
        }
        // ???????????????????????????
        QueryWrapper<StoreManager> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(StoreManager::getStoreManagerPhone,phone).eq(StoreManager::getShopId,shopId);

        RpcContext.getContext().setAttachment("shopId",shopId);

        StoreManager storeManager = this.getOne(queryWrapper);
        if(storeManager == null){
            result.setStatus(ResultCode.error);
            result.setDesc("????????????????????????");
            return result ;
        }
        // ??????????????????????????????
        List<Store> stores = storeManager.getStores();
        if(stores == null || stores.isEmpty() ){
            result.setStatus(ResultCode.error);
            result.setDesc("??????????????????");
            return result;
        }
        Store store = stores.get(0);
        // ????????????
        String salts = MD5CryptUtil.getSalts(storeManager.getPassword());
        if(!Md5Crypt.md5Crypt(loginPass.getBytes()).equals(storeManager.getPassword())){
            result.setStatus(ResultCode.error);
            result.setDesc("???????????????");
            return result;
        }
        // ????????????
        Map<String,Object> tokenMap = Maps.newHashMap();
        tokenMap.put("shopId",shopId);
        tokenMap.put("storeId",store.getStoreId());
        tokenMap.put("loginUserId",storeManager.getStoreManagerId());
        tokenMap.put("loginUserName",storeManager.getStoreManagerName());
        tokenMap.put("userType",SystemCode.USER_TYPE_STORE_MANAGER);
        String tokenInfo ;
        try {
            tokenInfo = JWTUtil.createJWTByObj(tokenMap,secret);
        }catch(Exception e){
            e.printStackTrace();
            result.setStatus(ResultCode.error);
            result.setDesc("??????????????????");
            return result;
        }
        result.setStatus(ResultCode.success);
        result.setDesc("ok");
        result.setData(storeManager);
        result.setToken(tokenInfo);
        return result;
    }

    private void sendEmail( String shopId, String pwd,String email) {
        EmailObject emailObject = new EmailObject();
        emailObject.setEmail(email);
        emailObject.setSubject("????????????????????????");
        templateCode = templateCode.replace("shopId",shopId);
        templateCode = templateCode.replace("password",pwd);
        emailObject.setContent(templateCode);
        rabbitTemplate.convertAndSend(RabbitMQConfig.ACCOUNT_QUEUE, JSON.toJSONString(emailObject));
    }
}
