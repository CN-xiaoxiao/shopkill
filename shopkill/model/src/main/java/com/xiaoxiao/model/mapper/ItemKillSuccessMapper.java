package com.xiaoxiao.model.mapper;

import com.xiaoxiao.model.entity.ItemKillSuccess;
import org.apache.ibatis.annotations.Param;

public interface ItemKillSuccessMapper {
    int countByKillUserId(@Param("killId") Integer killId, @Param("userId") Integer userId);

    int insert(ItemKillSuccess itemKillSuccess);
}
