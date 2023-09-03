package com.xiaoxiao.server.service;

import com.xiaoxiao.model.dto.ItemKillSuccessUserInfo;
import com.xiaoxiao.model.dto.MailDto;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ接收消息的服务
 */
@Slf4j
@Service
public class RabbitReceiverService {

    @Resource
    private MailService mailService;

    @Resource
    private Environment env;

    /**
     * 接收信息
     * @param info
     */
    // TODO 有个报错 Failed to convert message
    // 感觉问题不大
    @RabbitListener(queues = {"${mq.kill.item.success.email.queue}"},containerFactory = "singleListenerContainer")
    public void consumeEmailMsg(ItemKillSuccessUserInfo info){
        try {
            log.info("秒杀异步邮件通知-接收消息:{}", info);

            // TODO 发送邮件
            String content = String.format(env.getProperty("mail.kill.item.success.content"), info.getItemName(), info.getCode());
            MailDto dto = new MailDto(env.getProperty("mail.kill.item.success.subject"), content, new String[]{info.getEmail()});
//            mailService.sendSimpleEmail(dto);
            mailService.sendHTMLMail(dto);

        } catch (Exception e) {
            log.error("秒杀异步邮件通知-接收消息-发生异常：", e.fillInStackTrace());
        }
    }
}
