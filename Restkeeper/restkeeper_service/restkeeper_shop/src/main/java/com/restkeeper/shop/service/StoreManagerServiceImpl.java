package com.restkeeper.shop.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.email.EmailObject;
import com.restkeeper.shop.config.RabbitMQConfig;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.entity.StoreManager;
import com.restkeeper.shop.mapper.StoreManagerMapper;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service(version = "1.0.0",protocol = "dubbo")
public class StoreManagerServiceImpl extends ServiceImpl<StoreManagerMapper, StoreManager> implements IStoreManagerService {
    @Autowired
    @Qualifier("storeService")
    private IStoreService storeService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${email.operator.templateCode}")
    private String templateCode;

    @Override
    public IPage<StoreManager> queryPageByCriteria(int pageNo, int pageSize, String criteria) {
        QueryWrapper<StoreManager> queryWrapper = new QueryWrapper();
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
            //修改store，一个门店只能一个管理员
            String storeManagerId = manager.getStoreManagerId();
            UpdateWrapper<Store> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().in(Store::getStoreId,storeIds).set(Store::getStoreManagerId,storeManagerId);
            flag = storeService.update(updateWrapper);
            if(flag){
                // 新增管理员成功并且关联门店成功
                // 发送邮件
                sendEmail(phone, manager.getShopId(),pwd,email);
            }
        }catch (Exception e){
            flag = false ;
            throw e ;
        }
        return flag;
    }

    private void sendEmail(String phone, String shopId, String pwd,String email) {
        EmailObject emailObject = new EmailObject();
        emailObject.setEmail(email);
        emailObject.setSubject("店长账号密码下发");
        templateCode = templateCode.replace("shopId",shopId);
        templateCode = templateCode.replace("password",pwd);
        emailObject.setContent(templateCode);
        rabbitTemplate.convertAndSend(RabbitMQConfig.ACCOUNT_QUEUE, JSON.toJSONString(emailObject));
    }
}
