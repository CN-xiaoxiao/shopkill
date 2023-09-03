package com.xiaoxiao.server.controller;

import com.github.pagehelper.PageInfo;
import com.xiaoxiao.api.utils.ResultUtils;
import com.xiaoxiao.api.utils.ResultVo;
import com.xiaoxiao.model.dto.KillDto;
import com.xiaoxiao.model.entity.ItemKill;
import com.xiaoxiao.model.wrap.ItemKillParm;
import com.xiaoxiao.server.service.IItemKillService;
import jakarta.annotation.Resource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/itemkill")
public class ItemKillController {

    @Resource
    private IItemKillService itemKillService;


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

        Boolean res = itemKillService.killItem(dto.getKillId(),userId);

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
}
