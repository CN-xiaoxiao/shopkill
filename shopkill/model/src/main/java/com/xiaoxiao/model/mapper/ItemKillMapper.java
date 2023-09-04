package com.xiaoxiao.model.mapper;

import com.xiaoxiao.model.entity.ItemKill;
import com.xiaoxiao.model.wrap.ItemKillParm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemKillMapper {

    List<ItemKill> selectByPageAndCondition(ItemKillParm itemKillParm);

    ItemKill selectById(@Param("killId") Integer killId);

    int updateKillItem(@Param("killId") Integer killId);

    ItemKill selectOneById(@Param("killId") Integer killId);

    boolean insertKillItem(ItemKill itemKill);

    boolean updateIsActive(@Param("id") Integer id);
}
