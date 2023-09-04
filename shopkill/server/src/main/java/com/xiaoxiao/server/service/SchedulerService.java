package com.xiaoxiao.server.service;

import com.xiaoxiao.model.entity.ItemKillSuccess;
import com.xiaoxiao.model.mapper.ItemKillSuccessMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@Slf4j
public class SchedulerService {

    @Resource
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Resource
    private Environment env;

//    @Scheduled(cron = "0/10 * * * * ?")
    @Scheduled(cron = "0 0/30 * * * ?")
    public void schedulerExpireOrders() {
        try {
            List<ItemKillSuccess> list = itemKillSuccessMapper.selectExpireOrders();

            list.stream().forEach(itemKillSuccess -> {
                if (itemKillSuccess != null &&
                        itemKillSuccess.getDiffTime() >
                                env.getProperty("scheduler.expire.orders.time", Integer.class)) {
                    itemKillSuccessMapper.expireOrder(itemKillSuccess.getCode());
                }
            });
        } catch (Exception e) {
            log.error("定时获取status=0的订单，并判断是否超过TTL，然后进行失效-发生异常", e.fillInStackTrace());
        }
    }
}
