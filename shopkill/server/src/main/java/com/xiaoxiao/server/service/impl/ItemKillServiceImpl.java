package com.xiaoxiao.server.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoxiao.model.entity.ItemKill;
import com.xiaoxiao.model.entity.ItemKillSuccess;
import com.xiaoxiao.model.enums.SysConstant;
import com.xiaoxiao.model.mapper.ItemKillMapper;
import com.xiaoxiao.model.mapper.ItemKillSuccessMapper;
import com.xiaoxiao.model.wrap.ItemKillParm;
import com.xiaoxiao.model.wrap.ItemParm;
import com.xiaoxiao.server.service.IItemKillService;
import com.xiaoxiao.server.service.RabbitSenderService;
import com.xiaoxiao.server.utils.IdGenerator;
import jakarta.annotation.Resource;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemKillServiceImpl implements IItemKillService {

    @Resource
    private ItemKillMapper itemKillMapper;

    @Resource
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Resource
    private RabbitSenderService rabbitSenderService;

    @Override
    public PageInfo<ItemKill> selectQuestionInfoListByCondition(ItemKillParm itemKillParm) {
        //1.分页助手开始分页
        PageHelper.startPage(itemKillParm.getCurrentPage(), itemKillParm.getPageSize());
        //2.调用dao层的select查询方法，第一个select方法会被分页
        List<ItemKill> itemKills = itemKillMapper.selectByPageAndCondition(itemKillParm);
        //3。封装分页结果到PageInfo中
        return new PageInfo<>(itemKills, 10);
    }

    @Override
    public Boolean killItem(Integer killId, Integer userId) {
        Boolean result=false;

        // 判断当前用户是否已经抢购过当前商品
        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
            // 查询待秒杀商品详情
            ItemKill itemKill=itemKillMapper.selectById(killId);

            // 判断是否可以被秒杀canKill=1?
            if (itemKill!=null && 1==itemKill.getCanKill() ){
                // 扣减库存-减一
                int res=itemKillMapper.updateKillItem(killId);

                // 扣减是否成功?是-生成秒杀成功的订单，同时通知用户秒杀成功的消息
                if (res>0){
                    commonRecordKillSuccessInfo(itemKill,userId);
                    result=true;
                }
            }
        }
        return result;
    }

    /**
     * 记录用户秒杀成功后生成的订单，并进行异步邮件消息的通知
     * @param itemKill
     * @param userId
     */
    private void commonRecordKillSuccessInfo(ItemKill itemKill, Integer userId) {
        ItemKillSuccess itemKillSuccess = new ItemKillSuccess();

        String orderNo = String.valueOf(new IdGenerator(2,3).getId());

        itemKillSuccess.setCode(orderNo);
        itemKillSuccess.setItemId(itemKill.getItemId());
        itemKillSuccess.setKillId(itemKill.getId());
        itemKillSuccess.setUserId(userId.toString());
        itemKillSuccess.setStatus(SysConstant.OrderStatus.SuccessNotPayed.getCode().byteValue());
        itemKillSuccess.setCreateTime(DateTime.now().toDate());

        int res = itemKillSuccessMapper.insert(itemKillSuccess);

        if (res > 0) {
            // 进行异步邮件消息的通知
            rabbitSenderService.sendKillSuccessEmailMsg(orderNo);

            // 加入死信队列，用于超时指定的TTL时间时仍然未支付的订单。
            rabbitSenderService.sendKillSuccessOrderExpireMsg(orderNo);
        }

    }
}
