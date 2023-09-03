package com.xiaoxiao.model.mapper;

import com.xiaoxiao.model.entity.Item;
import com.xiaoxiao.model.wrap.ItemParm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemMapper {
    List<Item> selectByPageAndCondition(ItemParm itemParm);

    Boolean add(Item item);

    Item selectItemByItemName(@Param("name") String name);

    Boolean updateById(Item item);

    Boolean deleteById(@Param("id") Integer id);

    Item selectItemByItemId(@Param("id") Integer id);
}
