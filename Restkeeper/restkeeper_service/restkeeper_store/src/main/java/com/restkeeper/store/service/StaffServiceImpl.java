package com.restkeeper.store.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.email.EmailObject;
import com.restkeeper.store.mapper.StaffMapper;
import com.restkeeper.store.entity.Staff;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service(version = "1.0.0",protocol = "dubbo")
@RefreshScope
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements IStaffService {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${email.operator.templateCode}")
    private String templateCode;


    @Override
    @Transactional
    public boolean addStaff(Staff staff) {
        // 处理密码信息
        String password = staff.getPassword();
        if(StringUtils.isEmpty(password)){
            password = RandomStringUtils.randomNumeric(8);
        }
        staff.setPassword(Md5Crypt.md5Crypt(password.getBytes()));
        try {
            this.save(staff);
        }catch (Exception e){
            e.printStackTrace();
            return false ;
        }
        // 发送邮件
        sendEmail(staff.getShopId() , staff.getPassword(),staff.getEmail());
        return true ;
    }

    private void sendEmail( String shopId, String pwd,String email) {
        EmailObject emailObject = new EmailObject();
        emailObject.setEmail(email);
        emailObject.setSubject("店员账号密码下发");
        templateCode = templateCode.replace("shopId",shopId);
        templateCode = templateCode.replace("password",pwd);
        emailObject.setContent(templateCode);
        rabbitTemplate.convertAndSend(SystemCode.SMS_ACCOUNT_QUEUE, JSON.toJSONString(emailObject));
    }
}
