package com.xiaoxiao.server.service;

import com.xiaoxiao.model.dto.ItemKillSuccessUserInfo;
import com.xiaoxiao.model.mapper.ItemKillSuccessMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ发送消息的服务
 */
@Slf4j
@Service
public class RabbitSenderService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private Environment env;

    @Resource
    private ItemKillSuccessMapper itemKillSuccessMapper;

    /**
     * 秒杀成功异步发送邮件通知消息
     * @param orderNo 订单编号
     */
    public void sendKillSuccessEmailMsg(String orderNo) {
        log.info("秒杀成功异步发送邮件通知消息，准备发送消息：{}", orderNo);

        try {

            if (StringUtils.isNotEmpty(orderNo)) {
                ItemKillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNo);
                if (info != null) {
                    // Rabbitmq发送消息的逻辑
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.email.exchange"));
                    rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.email.routing.key"));

                    // 将 info 充当消息发送至队列
                    rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            MessageProperties messageProperties=message.getMessageProperties();
                            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,ItemKillSuccessUserInfo.class);
                            return message;
                        }
                    });
                }
            }



            Message message = MessageBuilder.withBody(orderNo.getBytes("UTF-8")).build();
            rabbitTemplate.convertAndSend(message);

        } catch (Exception e) {
            log.error("秒杀成功异步发送邮件通知消息，发生异常，消息为{}", orderNo, e.fillInStackTrace());
        }
    }
}
