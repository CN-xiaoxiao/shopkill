package com.xiaoxiao.model.mapper;

import com.xiaoxiao.model.dto.ItemKillSuccessUserInfo;
import com.xiaoxiao.model.entity.ItemKillSuccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ItemKillSuccessMapper {
    int countByKillUserId(@Param("killId") Integer killId, @Param("userId") Integer userId);

    int insert(ItemKillSuccess itemKillSuccess);

    ItemKillSuccessUserInfo selectByCode(@Param("code") String code);
}
