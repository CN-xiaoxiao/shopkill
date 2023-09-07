package com.xiaoxiao.server.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoxiao.model.entity.Item;
import com.xiaoxiao.model.entity.ItemKill;
import com.xiaoxiao.model.entity.ItemKillSuccess;
import com.xiaoxiao.model.enums.SysConstant;
import com.xiaoxiao.model.mapper.ItemKillMapper;
import com.xiaoxiao.model.mapper.ItemKillSuccessMapper;
import com.xiaoxiao.model.mapper.ItemMapper;
import com.xiaoxiao.model.wrap.ItemKillParm;
import com.xiaoxiao.server.service.IItemKillService;
import com.xiaoxiao.server.service.RabbitSenderService;
import com.xiaoxiao.server.utils.IdGenerator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.joda.time.DateTime;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ItemKillServiceImpl implements IItemKillService {

    @Resource
    private ItemKillMapper itemKillMapper;

    @Resource
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Resource
    private ItemMapper itemMapper;

    @Resource
    private RabbitSenderService rabbitSenderService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CuratorFramework curatorFramework;

    private static final String PATH_PREFIX = "/kill/zkLock/";

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
    public PageInfo<ItemKill> selectQuestionInfoAllListByCondition(ItemKillParm itemKillParm) {
        //1.分页助手开始分页
        PageHelper.startPage(itemKillParm.getCurrentPage(), itemKillParm.getPageSize());
        //2.调用dao层的select查询方法，第一个select方法会被分页
        List<ItemKill> itemKills = itemKillMapper.selectAllByPageAndCondition(itemKillParm);
        //3。封装分页结果到PageInfo中
        return new PageInfo<>(itemKills, 10);
    }

    /**
     * 根据id修改秒杀商品信息
     *
     * 先恢复商品的库存，然后再减去秒杀商品新的数量
     *
     * 总逻辑：添加秒杀商品的时候就从总库存中删除相应的秒杀商品的数量
     *
     * @param itemKill
     * @return
     */
    @Override
    @Transactional
    public boolean updateById(ItemKill itemKill) {
        // 数据库中的秒杀商品信息
        ItemKill itemKill1Dao = itemKillMapper.selectOneById(itemKill.getItemId());

        int newTotal = itemKill.getTotal();

        boolean flag2 = true;

        if (newTotal != itemKill1Dao.getTotal()) {
            // 数据库中的商品信息
            Item itemDao = itemMapper.selectItemByItemId(itemKill.getItemId());
            // 更新数据
            if (newTotal > (itemDao.getStock() + itemKill1Dao.getTotal())) {
                return false;
            }
            itemDao.setStock(itemDao.getStock() + itemKill1Dao.getTotal() - newTotal);
            itemDao.setUpdateTime(new Date());
            flag2 = itemMapper.updateById(itemDao);
        }

        boolean flag1 = itemKillMapper.updateKillItemByItemId(itemKill);

        if (flag1 && flag2) {
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public boolean deleteItemKillByItemId(Integer itemId) {

        Item itemDao = itemMapper.selectItemByItemId(itemId);
        ItemKill itemKillDao = itemKillMapper.selectOneById(itemId);
        itemDao.setStock(itemDao.getStock() + itemKillDao.getTotal());
        itemDao.setUpdateTime(new Date());

        boolean flag1 = itemMapper.updateStockById(itemDao);
        boolean flag2 = itemKillMapper.deleteItemKillByItemId(itemId);

        return flag1 && flag2;
    }


    /**
     * 秒杀业务的核心逻辑-使用redis实现分布式锁
     * @param killId
     * @param userId
     * @return
     */
    @Override
    public Boolean killItemRedis(Integer killId, Integer userId) {
        Boolean result=false;

        // 判断当前用户是否已经抢购过当前商品
        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){

            // 使用redis 的原子操作实现分布式锁
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            final String key = new StringBuffer().append(killId).append(userId).append("-RedisLock").toString();
            final String value = String.valueOf(IdGenerator.getIdGenerator().getId());
            Boolean aBoolean = valueOperations.setIfAbsent(key, value);

            if (aBoolean) {

                stringRedisTemplate.expire(key, 30, TimeUnit.SECONDS);

                try {
                    // 查询待秒杀商品详情
                    ItemKill itemKill=itemKillMapper.selectById(killId);

                    // 判断是否可以被秒杀canKill=1?
                    if (itemKill!=null && 1==itemKill.getCanKill() && itemKill.getTotal() > 0){
                        // 扣减库存-减一
                        int res=itemKillMapper.updateKillItem(killId);

                        // 扣减是否成功?是-生成秒杀成功的订单，同时通知用户秒杀成功的消息
                        if (res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);
                            result=true;
                        }
                    }
                } catch (Exception e) {
                    log.error("秒杀业务出问题了-redis实现分布式锁", e.fillInStackTrace());
                } finally {
                    if (value.equals(valueOperations.get(key).toString())) {
                        stringRedisTemplate.delete(key);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 秒杀核心-使用Redisson实现分布式锁
     * @param killId
     * @param userId
     * @return
     */
    @Override
    public Boolean killItemRedisson(Integer killId, Integer userId) {
        Boolean result=false;

        final String key = new StringBuffer().append(killId).append(userId).append("-RedissonLock").toString();

        RLock lock = redissonClient.getLock(key);

        try {
            boolean bLock = lock.tryLock(30, 10, TimeUnit.SECONDS);

            if (bLock) {
                // 判断当前用户是否已经抢购过当前商品
                if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){

                    // 查询待秒杀商品详情
                    ItemKill itemKill=itemKillMapper.selectById(killId);

                    // 判断是否可以被秒杀canKill=1?
                    if (itemKill!=null && 1==itemKill.getCanKill() && itemKill.getTotal() > 0){
                        // 扣减库存-减一
                        int res=itemKillMapper.updateKillItem(killId);

                        // 扣减是否成功?是-生成秒杀成功的订单，同时通知用户秒杀成功的消息
                        if (res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);
                            result=true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("秒杀业务出问题了-redisson实现分布式锁", e.fillInStackTrace());
        } finally {
            lock.unlock();
        }

        return result;
    }

    /**
     * 秒杀核心-使用zooKeep实现分布式锁
     * @param killId
     * @param userId
     * @return
     */
    @Override
    public Boolean killItemZookeeper(Integer killId, Integer userId) {

        Boolean result=false;

        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, PATH_PREFIX+killId+userId+"-lock");

        try {

            if (mutex.acquire(10L, TimeUnit.SECONDS)) {
                // 判断当前用户是否已经抢购过当前商品
                if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
                    // 查询待秒杀商品详情
                    ItemKill itemKill=itemKillMapper.selectById(killId);

                    // 判断是否可以被秒杀canKill=1?
                    if (itemKill!=null && 1==itemKill.getCanKill() && itemKill.getTotal() > 0){
                        // 扣减库存-减一
                        int res=itemKillMapper.updateKillItem(killId);

                        // 扣减是否成功?是-生成秒杀成功的订单，同时通知用户秒杀成功的消息
                        if (res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);
                            result=true;
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("秒杀业务出问题了-zookeeper实现分布式锁", e.fillInStackTrace());
        } finally {
            if (mutex!=null) {
                try {
                    mutex.release();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    @Override
    public ItemKill selectItemKillById(Integer killId) {
        if (killId == null || killId < 0) {
            return null;
        }

        return itemKillMapper.selectOneById(killId);
    }

    @Override
    @Transactional
    public boolean insertKillItem(ItemKill itemKill) {

        int total = itemKill.getTotal();
        Item itemDao = itemMapper.selectItemByItemName(itemKill.getItemName());

        if (total > itemDao.getStock()) {
            return false;
        }

        if (itemKill.getItemName() == null ||
                itemKill.getItemName().equals("") ||
                !itemDao.getName().equals(itemKill.getItemName())) {
            return false;
        }

        Date date = new Date();
        itemDao.setUpdateTime(date);
        itemDao.setStock(itemDao.getStock() - total);

        itemKill.setCreateTime(date);
        itemKill.setItemId(itemDao.getId());

        // 更新item库存
        boolean flag = itemMapper.updateStockById(itemDao);

        return itemKillMapper.insertKillItem(itemKill) && flag;
    }

    @Override
    public boolean updateIsActive(Integer id) {
        return itemKillMapper.updateIsActive(id);
    }

    /**
     * 记录用户秒杀成功后生成的订单，并进行异步邮件消息的通知
     * @param itemKill
     * @param userId
     */
    private void commonRecordKillSuccessInfo(ItemKill itemKill, Integer userId) {
        ItemKillSuccess itemKillSuccess = new ItemKillSuccess();

        String orderNo = String.valueOf(IdGenerator.getIdGenerator().getId());

        itemKillSuccess.setCode(orderNo);
        itemKillSuccess.setItemId(itemKill.getItemId());
        itemKillSuccess.setKillId(itemKill.getId());
        itemKillSuccess.setUserId(userId.toString());
        itemKillSuccess.setStatus(SysConstant.OrderStatus.SuccessNotPayed.getCode().byteValue());
        itemKillSuccess.setCreateTime(DateTime.now().toDate());

        if (itemKillSuccessMapper.countByKillUserId(itemKill.getId(), userId) <= 0) {
            int res = itemKillSuccessMapper.insert(itemKillSuccess);

            if (res > 0) {
                // 进行异步邮件消息的通知
                rabbitSenderService.sendKillSuccessEmailMsg(orderNo);

                // 加入死信队列，用于超时指定的TTL时间时仍然未支付的订单。
                rabbitSenderService.sendKillSuccessOrderExpireMsg(orderNo);
            }
        }

    }
}
