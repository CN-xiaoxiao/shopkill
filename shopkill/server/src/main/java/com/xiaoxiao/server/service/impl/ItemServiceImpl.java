package com.xiaoxiao.server.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoxiao.model.entity.Item;
import com.xiaoxiao.model.mapper.ItemKillMapper;
import com.xiaoxiao.model.mapper.ItemMapper;
import com.xiaoxiao.model.wrap.ItemParm;
import com.xiaoxiao.server.service.IItemService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements IItemService {

    @Resource
    private ItemMapper itemMapper;


    @Override
    public PageInfo<Item> selectQuestionInfoListByCondition(ItemParm itemParm) {
        //1.分页助手开始分页
        PageHelper.startPage(itemParm.getCurrentPage(), itemParm.getPageSize());
        //2.调用dao层的select查询方法，第一个select方法会被分页
        List<Item> items = itemMapper.selectByPageAndCondition(itemParm);
        //3。封装分页结果到PageInfo中
        return new PageInfo<>(items, 10);
    }

    @Override
    public Boolean add(Item item) {

        Item itemDao = itemMapper.selectItemByItemName(item.getName());

        if (itemDao != null) {
            return false;
        }

        return itemMapper.add(item);

    }

    @Override
    public Boolean updateById(Item item) {
        return itemMapper.updateById(item);
    }

    @Override
    public Boolean deleteById(Integer id) {
        return itemMapper.deleteById(id);
    }

    @Override
    public Item selectItemById(Integer id) {
        return itemMapper.selectItemByItemId(id);
    }
}