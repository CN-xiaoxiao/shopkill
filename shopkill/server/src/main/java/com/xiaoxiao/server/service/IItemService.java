package com.xiaoxiao.server.service;

import com.github.pagehelper.PageInfo;
import com.xiaoxiao.model.entity.Item;
import com.xiaoxiao.model.wrap.ItemParm;

public interface IItemService {
    PageInfo<Item> selectQuestionInfoListByCondition(ItemParm itemParm);

    Boolean add(Item item);

    Boolean updateById(Item item);

    Boolean deleteById(Integer id);

    Item selectItemById(Integer id);
}
