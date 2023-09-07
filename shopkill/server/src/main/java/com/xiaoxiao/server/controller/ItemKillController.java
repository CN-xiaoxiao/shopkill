package com.xiaoxiao.server.controller;

import com.github.pagehelper.PageInfo;
import com.xiaoxiao.api.utils.ResultUtils;
import com.xiaoxiao.api.utils.ResultVo;
import com.xiaoxiao.model.dto.ItemKillSuccessUserInfo;
import com.xiaoxiao.model.dto.KillDto;
import com.xiaoxiao.model.entity.ItemKill;
import com.xiaoxiao.model.mapper.ItemKillSuccessMapper;
import com.xiaoxiao.model.wrap.ItemKillParm;
import com.xiaoxiao.server.service.IItemKillService;
import jakarta.annotation.Resource;
import org.apache.zookeeper.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/itemkill")
public class ItemKillController {

    @Autowired
    private IItemKillService itemKillService;

    @Resource
    private ItemKillSuccessMapper itemKillSuccessMapper;

    /**
     * 秒杀的核心逻辑
     * @param dto
     * @param result
     * @return
     */
    @PostMapping("/execute")
    public ResultVo execute(@RequestBody @Validated KillDto dto, BindingResult result){
        if (result.hasErrors() || dto.getKillId()<=0){
            return ResultUtils.error("非法的参数", 201);
        }

        Integer userId=dto.getUserId();

        Boolean res = itemKillService.killItemZookeeper(dto.getKillId(),userId);

        if (!res) {
            return ResultUtils.error("商品抢购完毕或不在抢购时间段内", -1);
        }

        return ResultUtils.success("抢购成功！");

    }

    /**
     * 压力测试
     * @param dto
     * @param result
     * @return
     */
    @PostMapping("/execute/lock")
    public ResultVo executeLock(@RequestBody @Validated KillDto dto, BindingResult result){
        if (result.hasErrors() || dto.getKillId()<=0){
            return ResultUtils.error("非法的参数", 201);
        }

        Integer userId=dto.getUserId();

        Boolean res = itemKillService.killItemRedisson(dto.getKillId(),userId);

        if (!res) {
            return ResultUtils.error("商品抢购完毕或不在抢购时间段内", -1);
        }

        return ResultUtils.success("抢购成功！");

    }


    /**
     * 分页查询
     * @param itemKillParm
     * @return
     */
    @GetMapping("/list")
    public ResultVo getListByCondition(ItemKillParm itemKillParm){

        PageInfo<ItemKill> itemKillParmPageInfo = itemKillService.selectQuestionInfoListByCondition(itemKillParm);

        return ResultUtils.success("查询成功", itemKillParmPageInfo);
    }

    /**
     * 所有有效的秒杀商品
     * @param itemKillParm
     * @return
     */
    @GetMapping("/allList")
    public ResultVo getAllListByCondition(ItemKillParm itemKillParm){

        PageInfo<ItemKill> itemKillParmPageInfo = itemKillService.selectQuestionInfoAllListByCondition(itemKillParm);

        return ResultUtils.success("查询成功", itemKillParmPageInfo);
    }

    /**
     * 查看订单详情
     * @param orderNo 订单编号
     * @return
     */
    @GetMapping("/record/detail")
    public ResultVo killRecordDetail(@RequestParam("orderNo") String orderNo) {

        if (StringUtils.isBlank(orderNo)) {
            return ResultUtils.error("订单编号出错！");
        }

        ItemKillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNo);

        if (info == null) {
            return ResultUtils.error("未找到相应的订单!");
        }

        return ResultUtils.success("查询成功！", info);
    }

    /**
     * 编辑秒杀商品
     * @param itemKill
     * @return
     */
    @PutMapping
    public ResultVo edit(@RequestBody ItemKill itemKill) {
        boolean flag = itemKillService.updateById(itemKill);

        if (!flag) {
            return ResultUtils.error("更新失败!");
        }

        return ResultUtils.success("更新成功！");
    }

    /**
     * 增加秒杀商品
     * @param itemKill
     * @return
     */
    @PostMapping
    public ResultVo add(@RequestBody ItemKill itemKill) {
        boolean flag = itemKillService.insertKillItem(itemKill);

        if (!flag) {
            return ResultUtils.error("更新失败!");
        }

        return ResultUtils.success("更新成功！");
    }

    /**
     * 删除秒杀商品（真删除）
     * @param itemId
     * @return
     */
    @DeleteMapping("/{itemId}")
    public ResultVo delete(@PathVariable("itemId") Integer itemId) {

        boolean flag = itemKillService.deleteItemKillByItemId(itemId);

        if (!flag) {
            return ResultUtils.error("删除失败!");
        }

        return ResultUtils.success("删除成功！");
    }
}
