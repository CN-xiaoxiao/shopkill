package com.xiaoxiao.server.controller;

import com.github.pagehelper.PageInfo;
import com.xiaoxiao.api.utils.ResultUtils;
import com.xiaoxiao.api.utils.ResultVo;
import com.xiaoxiao.model.entity.Item;
import com.xiaoxiao.model.entity.ItemKill;
import com.xiaoxiao.model.wrap.ItemParm;
import com.xiaoxiao.server.service.IItemKillService;
import com.xiaoxiao.server.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Autowired
    private IItemService itemService;

    @Autowired
    private IItemKillService itemKillService;

    /**
     * 添加商品
     * @param item
     * @return
     */
    @PostMapping
    public ResultVo add(@RequestBody Item item){
        Boolean flag = itemService.add(item);

        if (flag) {
            return ResultUtils.success("添加成功！");
        } else {
            return ResultUtils.error("添加失败！");
        }
    }

    /**
     * 编辑商品
     * @param item
     * @return
     */
    @PutMapping
    public ResultVo edit(@RequestBody Item item) {
        Boolean flag = itemService.updateById(item);

        if (flag) {
            return ResultUtils.success("编辑成功！");
        } else {
            return ResultUtils.error("编辑失败！");
        }
    }

    /**
     * 删除商品
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Integer id) {

        Boolean flag = itemService.deleteById(id);

        if (flag) {
            return ResultUtils.success("删除成功！");
        } else {
            return ResultUtils.error("删除失败！");
        }
    }

    /**
     * 根据商品id 获取商品详情
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    public ResultVo detail(@PathVariable("id") Integer id) {
        if (id ==null || id < 0) {
            return ResultUtils.error("获取商品详情失败！");
        }

        Item item = itemService.selectItemById(id);

        if (item != null) {
            return ResultUtils.success("查询成功！", item);
        } else {
            return ResultUtils.error("查询失败！");
        }
    }

    /**
     * 分页查询
     * @param itemParm
     * @return
     */
    @GetMapping("/list")
    public ResultVo getListByCondition(ItemParm itemParm){

        PageInfo<Item> itemParmPageInfo = itemService.selectQuestionInfoListByCondition(itemParm);

        return ResultUtils.success("查询成功", itemParmPageInfo);
    }

    /**
     * 添加新的秒杀商品（真添加）
     * @param itemKill
     * @return
     */
    @PutMapping("/addKill")
    public ResultVo setKillItem(@RequestBody ItemKill itemKill) {

        if (itemKill.getItemId() == null || itemKill.getItemId()< 0) {
            return ResultUtils.error("商品编号错误！");
        }

        Item itemDao = itemService.selectItemById(itemKill.getItemId());
        ItemKill itemKillDao = itemKillService.selectItemKillById(itemKill.getItemId() );

        // TODO 判断is_active是否为0，如果是，就不用真正的添加，直接置为1即可
        if (itemDao == null || itemKillDao != null) {
            return ResultUtils.error("添加秒杀商品失败！");
        }

        boolean flag = itemKillService.insertKillItem(itemKill);

        if (flag) {
            return ResultUtils.success("添加成功！");
        } else {
            return ResultUtils.error("添加失败！");
        }
    }

    /**
     * 删除秒杀物品（取消秒杀）伪删除 is_active = 0
     * @param id 商品编号
     * @return
     */
    @PutMapping("/deleteKill/{id}")
    public ResultVo deleteKillItem(@PathVariable("id") Integer id) {
        ItemKill itemKillDao = itemKillService.selectItemKillById(id);

        if (itemKillDao == null) {
            return ResultUtils.error("删除失败！");
        }

        boolean flag = itemKillService.updateIsActive(id);

        if (flag) {
            return ResultUtils.success("删除成功！");
        } else {
            return ResultUtils.error("删除失败！");
        }
    }
}
