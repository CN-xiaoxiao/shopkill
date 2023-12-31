package com.xiaoxiao.model.mapper;

import com.xiaoxiao.model.dto.ItemKillSuccessUserInfo;
import com.xiaoxiao.model.entity.ItemKillSuccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ItemKillSuccessMapper {
    int countByKillUserId(@Param("killId") Integer killId, @Param("userId") Integer userId);

    int insert(ItemKillSuccess itemKillSuccess);

    ItemKillSuccessUserInfo selectByCode(@Param("code") String code);

    ItemKillSuccess selectByPrimaryKey(@Param("code") String code);

    int expireOrder(String code);

    List<ItemKillSuccess> selectExpireOrders();
}
