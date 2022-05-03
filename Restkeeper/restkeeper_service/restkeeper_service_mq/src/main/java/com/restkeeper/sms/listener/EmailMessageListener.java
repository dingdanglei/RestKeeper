package com.restkeeper.sms.listener;

import com.alibaba.fastjson.JSON;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.email.EmailObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailMessageListener {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String account;

    @RabbitListener(queues = SystemCode.SMS_ACCOUNT_QUEUE)
    public void getAccountMessage(String message){

        log.info("发送短信监听类接收到了消息："+message);

        //转换参数
        EmailObject emailObject = JSON.parseObject(message,EmailObject.class);

        // 发送邮件
        try {
            boolean flag = sendSimpleMail(emailObject.getEmail(),emailObject.getSubject() , emailObject.getContent());
            if(flag){
                log.info("发送邮件成功");
            }else{
                log.info("发送邮件失败");
            }
        }catch(Exception e){
            log.info("发送邮件失败："+e.getMessage());
        }
    }






    public boolean sendSimpleMail(String address, String subject, String body) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(account);
        smm.setTo(address);
        smm.setSubject(subject);
        smm.setText(body);
        javaMailSender.send(smm);
        return true;
    }

    public boolean sendAttachmentMail(String address, String subject, String body, MultipartFile attach) throws MessagingException, IOException {
        MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
        mimeMessageHelper.setFrom(account);
        mimeMessageHelper.setTo(new String[]{address});
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(body);
        //文件路径
        byte[] bytes = attach.getBytes();
        String name = attach.getName();
        mimeMessageHelper.addAttachment(name, new ByteArrayResource(bytes));
        log.info("fileName:{}", name);
        javaMailSender.send(mimeMailMessage);
        return true;
    }
}
